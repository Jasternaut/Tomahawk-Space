import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';
import 'dart:io';
import 'package:http/io_client.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

Future<Apod> fetchApod({String? date}) async {
  final prefs = await SharedPreferences.getInstance();
  final secureStorage = const FlutterSecureStorage();
  final String apiKey = prefs.getString('nasa_api_key') ?? 'DEMO_KEY';
  String url = 'https://api.nasa.gov/planetary/apod?api_key=$apiKey';

  if (date != null && date.isNotEmpty) {
    url += '&date=$date';
  }

  // Настройка прокси
  final useProxy = prefs.getBool('use_proxy') ?? false;
  http.Client client = http.Client();

  if (useProxy) {
    final proxyAddress = prefs.getString('proxy_address') ?? '';
    final proxyPort = prefs.getString('proxy_port') ?? '';
    final proxyUsername = prefs.getString('proxy_username') ?? '';
    final proxyPassword = await secureStorage.read(key: 'proxy_password') ?? '';

    if (proxyAddress.isNotEmpty && proxyPort.isNotEmpty) {
      String proxy = '$proxyAddress:$proxyPort';
      if (proxyUsername.isNotEmpty && proxyPassword.isNotEmpty) {
        proxy = '$proxyUsername:$proxyPassword@$proxy';
      }
      HttpClient httpClient = HttpClient();
      httpClient.findProxy = (uri) => 'PROXY $proxy; DIRECT';
      client = IOClient(httpClient);
    }
  }

  try {
    final response = await client.get(Uri.parse(url));
    if (response.statusCode == 200) {
      return Apod.fromJson(jsonDecode(response.body));
    } else {
      throw Exception(
        'Не удалось загрузить изображение дня: ${response.statusCode}',
      );
    }
  } finally {
    client.close();
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> with AutomaticKeepAliveClientMixin {
  late Future<Apod>? futureApod;
  String? _selectedDate;
  Apod? currentApod;
  bool _isDateSelectionEnabled = false;
  final TextEditingController _dateController = TextEditingController();
  final Box<Apod> favoritesBox = Hive.box<Apod>('favorites');
  static Apod? _cachedApod;

  @override
  bool get wantKeepAlive => true; // Сохраняем состояние экрана

  @override
  void initState() {
    super.initState();
    if (_selectedDate == null && _cachedApod == null) {
      _loadApod();
    } else if (_selectedDate == null && _cachedApod != null) {
      // Используем кэш
      setState(() {
        currentApod = _cachedApod;
        futureApod = Future.value(_cachedApod);
      });
    } else {
      // Если дата выбрана, загружаем как обычно
      _loadApod();
    }
  }

  @override
  void dispose() {
    _dateController.dispose();
    super.dispose();
  }

  Future<void> _loadApod() async {
    setState(() {
      futureApod = fetchApod(date: _selectedDate)
          .then((apod) {
            setState(() {
              currentApod = apod;
              if (_selectedDate == null) {
                _cachedApod = apod;
              }
            });
            return apod;
          })
          .catchError((error) {
            setState(() {
              currentApod = null;
            });
            throw error;
          });
    });
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return Scaffold(
      backgroundColor: colorScheme.surfaceContainerLow,
      appBar: AppBar(
        title: Image.asset(
          theme.brightness == Brightness.dark
              ? 'assets/icons/logo_dark.png'
              : 'assets/icons/logo_light.png',
          height: 24,
        ),
        centerTitle: true,
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                ListTile(
                  title: Text(
                    'Указать конкретную дату',
                    style: textTheme.titleMedium?.copyWith(
                      color: colorScheme.onSurface,
                    ),
                  ),
                  trailing: Switch(
                    value: _isDateSelectionEnabled,
                    onChanged: (bool newValue) {
                      setState(() {
                        _isDateSelectionEnabled = newValue;
                        if (!_isDateSelectionEnabled) {
                          _selectedDate = null;
                          _dateController.clear(); 
                          if (_cachedApod != null) {
                            currentApod = _cachedApod;
                            futureApod = Future.value(_cachedApod);
                          } else {
                            _loadApod();
                          }
                        }
                      });
                    },
                    activeThumbColor: colorScheme.primary,
                    activeTrackColor: colorScheme.primaryContainer,
                  ),
                ),
                if (_isDateSelectionEnabled)
                  Builder(
                    builder: (BuildContext innerContext) {
                      return GestureDetector(
                        onTap: () async {
                          final DateTime? pickedDate = await showDatePicker(
                            context: innerContext,
                            initialDate: DateTime.now(),
                            firstDate: DateTime(1995, 6, 16),
                            lastDate: DateTime.now(),
                            helpText: 'Выберите дату',
                            cancelText: 'Отмена',
                            confirmText: 'Выбрать',
                            builder: (context, child) {
                              return Theme(
                                data: Theme.of(context).copyWith(
                                  colorScheme: colorScheme,
                                  textButtonTheme: TextButtonThemeData(
                                    style: TextButton.styleFrom(
                                      foregroundColor: colorScheme.primary,
                                    ),
                                  ),
                                ),
                                child: child!,
                              );
                            },
                          );
                          if (pickedDate != null) {
                            setState(() {
                              _selectedDate =
                                  "${pickedDate.year}-${pickedDate.month.toString().padLeft(2, '0')}-${pickedDate.day.toString().padLeft(2, '0')}";
                              _dateController.text = _selectedDate!;
                              _loadApod(); // Загружаем для выбранной даты
                            });
                          }
                        },
                        child: AbsorbPointer(
                          child: TextFormField(
                            controller: _dateController,
                            decoration: InputDecoration(
                              labelText: 'Дата (ГГГГ-ММ-ДД)',
                              border: const OutlineInputBorder(),
                              suffixIcon: Icon(
                                Icons.calendar_today,
                                color: colorScheme.primary,
                              ),
                              enabled: false,
                            ),
                            style: textTheme.bodyLarge?.copyWith(
                              color: colorScheme.onSurface,
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                
              ],
            ),
          ),
          Expanded(
            child: Center(
              child: FutureBuilder<Apod>(
                future: futureApod,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return CircularProgressIndicator(
                      color: colorScheme.primary,
                    );
                  } else if (snapshot.hasError) {
                    return Text(
                      'Ошибка: ${snapshot.error}',
                      textAlign: TextAlign.center,
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.error,
                      ),
                    );
                  } else if (snapshot.hasData) {
                    final apod = snapshot.data!;
                    return SingleChildScrollView(
                      padding: const EdgeInsets.all(16.0),
                      child: LayoutBuilder(
                        builder: (context, constraints) {
                          if (constraints.maxWidth > 900) {
                            return Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Expanded(
                                  flex: 1,
                                  child: apod.mediaType == 'video'
                                      ? Column(
                                          children: [
                                            Text(
                                              'Это видео, а не изображение. Откройте по ссылке:',
                                              textAlign: TextAlign.center,
                                              style: textTheme.bodyLarge
                                                  ?.copyWith(
                                                    color:
                                                        colorScheme.onSurface,
                                                  ),
                                            ),
                                            const SizedBox(height: 8),
                                            InkWell(
                                              onTap: () async {
                                                final Uri uri = Uri.parse(
                                                  apod.url,
                                                );
                                                if (await canLaunchUrl(uri)) {
                                                  await launchUrl(uri);
                                                } else {
                                                  ScaffoldMessenger.of(
                                                    context,
                                                  ).showSnackBar(
                                                    SnackBar(
                                                      content: Text(
                                                        'Не удалось открыть ссылку: ${apod.url}',
                                                        style: textTheme
                                                            .bodyMedium
                                                            ?.copyWith(
                                                              color: colorScheme
                                                                  .onErrorContainer,
                                                            ),
                                                      ),
                                                      backgroundColor:
                                                          colorScheme
                                                              .errorContainer,
                                                    ),
                                                  );
                                                }
                                              },
                                              child: Text(
                                                apod.url,
                                                textAlign: TextAlign.center,
                                                style: textTheme.bodyLarge
                                                    ?.copyWith(
                                                      color:
                                                          colorScheme.primary,
                                                      decoration: TextDecoration
                                                          .underline,
                                                    ),
                                              ),
                                            ),
                                          ],
                                        )
                                      : CachedNetworkImage(
                                          imageUrl: apod.url,    
                                          imageBuilder: (context, imageProvider) => Container(
                                            decoration: BoxDecoration(
                                              image: DecorationImage(
                                                image: imageProvider,
                                                fit: BoxFit.cover,
                                              ),
                                              borderRadius: BorderRadius.circular(12),
                                            ),
                                            height: 400,
                                          ),
                                          placeholder: (context, url) =>
                                              CircularProgressIndicator(
                                                color: colorScheme.primary,
                                              ),
                                          errorWidget: (context, url, error) =>
                                              Text(
                                                'Не удалось загрузить изображение.',
                                                textAlign: TextAlign.center,
                                                style: textTheme.bodyLarge
                                                    ?.copyWith(
                                                      color: colorScheme.error,
                                                    ),
                                              ),
                                        ),
                                ),
                                Expanded(
                                  flex: 1,
                                  child: Padding(
                                    padding: const EdgeInsets.all(8.0),
                                    child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          apod.title,
                                          style: textTheme.headlineSmall
                                              ?.copyWith(
                                                color: colorScheme.onSurface,
                                                fontWeight: FontWeight.bold,
                                              ),
                                        ),
                                        const SizedBox(height: 16),
                                        Text(
                                          apod.explanation,
                                          textAlign: TextAlign.justify,
                                          style: textTheme.bodyLarge?.copyWith(
                                            color: colorScheme.onSurface,
                                          ),
                                        ),
                                        const SizedBox(height: 8),
                                        Text(
                                          'Дата: ${apod.date}',
                                          style: textTheme.bodyMedium?.copyWith(
                                            color: colorScheme.onSurfaceVariant,
                                            fontStyle: FontStyle.italic,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ],
                            );
                          } else {
                            return Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Text(
                                  apod.title,
                                  textAlign: TextAlign.center,
                                  style: textTheme.headlineSmall?.copyWith(
                                    color: colorScheme.onSurface,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                                const SizedBox(height: 16),
                                apod.mediaType == 'video'
                                    ? Column(
                                        children: [
                                          Text(
                                            'Это видео, а не изображение. Откройте по ссылке:',
                                            textAlign: TextAlign.center,
                                            style: textTheme.bodyLarge
                                                ?.copyWith(
                                                  color: colorScheme.onSurface,
                                                ),
                                          ),
                                          const SizedBox(height: 8),
                                          InkWell(
                                            onTap: () async {
                                              final Uri uri = Uri.parse(
                                                apod.url,
                                              );
                                              if (await canLaunchUrl(uri)) {
                                                await launchUrl(uri);
                                              } else {
                                                ScaffoldMessenger.of(
                                                  context,
                                                ).showSnackBar(
                                                  SnackBar(
                                                    content: Text(
                                                      'Не удалось открыть ссылку: ${apod.url}',
                                                      style: textTheme
                                                          .bodyMedium
                                                          ?.copyWith(
                                                            color: colorScheme
                                                                .onErrorContainer,
                                                          ),
                                                    ),
                                                    backgroundColor: colorScheme
                                                        .errorContainer,
                                                  ),
                                                );
                                              }
                                            },
                                            child: Text(
                                              apod.url,
                                              textAlign: TextAlign.center,
                                              style: textTheme.bodyLarge
                                                  ?.copyWith(
                                                    color: colorScheme.primary,
                                                    decoration: TextDecoration
                                                        .underline,
                                                  ),
                                            ),
                                          ),
                                        ],
                                      )
                                    : CachedNetworkImage(
                                        imageUrl: apod.url,
                                        imageBuilder: (context, imageProvider) => Container(
                                            decoration: BoxDecoration(
                                              image: DecorationImage(
                                                image: imageProvider,
                                                fit: BoxFit.cover,
                                              ),
                                              borderRadius: BorderRadius.circular(12),
                                            ),
                                            height: 400,
                                          ),
                                        placeholder: (context, url) =>
                                            CircularProgressIndicator(
                                              color: colorScheme.primary,
                                            ),
                                        errorWidget: (context, url, error) =>
                                            Text(
                                              'Не удалось загрузить изображение.',
                                              textAlign: TextAlign.center,
                                              style: textTheme.bodyLarge
                                                  ?.copyWith(
                                                    color: colorScheme.error,
                                                  ),
                                            ),
                                      ),
                                const SizedBox(height: 16),
                                Text(
                                  apod.explanation,
                                  textAlign: TextAlign.justify,
                                  style: textTheme.bodyLarge?.copyWith(
                                    color: colorScheme.onSurface,
                                  ),
                                ),
                                const SizedBox(height: 8),
                                Text(
                                  'Дата: ${apod.date}',
                                  style: textTheme.bodyMedium?.copyWith(
                                    color: colorScheme.onSurfaceVariant,
                                    fontStyle: FontStyle.italic,
                                  ),
                                ),
                              ],
                            );
                          }
                        },
                      ),
                    );
                  } else {
                    return Text(
                      'Нажмите "Загрузить" для получения изображения.',
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                    );
                  }
                },
              ),
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(
              horizontal: 16.0,
              vertical: 8.0,
            ),
            decoration: BoxDecoration(
              color: colorScheme.surfaceContainer,
              boxShadow: [
                BoxShadow(
                  color: colorScheme.shadow.withValues(alpha: 0.1),
                  blurRadius: 4,
                  offset: const Offset(0, -2),
                ),
              ],
            ),
            child: Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () async {
                      HapticFeedback.lightImpact();
                      await _loadApod();
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: colorScheme.primary,
                      foregroundColor: colorScheme.onPrimary,
                      padding: const EdgeInsets.symmetric(vertical: 16.0),
                    ),
                    child: Text(
                      'Загрузить',
                      style: textTheme.labelLarge?.copyWith(
                        color: colorScheme.onPrimary,
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                if (currentApod != null)
                  IconButton(
                    style: IconButton.styleFrom(
                      backgroundColor: colorScheme.primaryContainer,
                      foregroundColor: colorScheme.onPrimaryContainer,
                    ),
                    icon: Icon(
                      favoritesBox.containsKey(currentApod!.date)
                          ? Icons.favorite
                          : Icons.favorite_border,
                      color: colorScheme.primary,
                    ),
                    onPressed: () async {
                      HapticFeedback.lightImpact();
                      if (favoritesBox.containsKey(currentApod!.date)) {
                        await favoritesBox.delete(currentApod!.date);
                        showCustomNotification(
                          context,
                          'Удалено из избранного',
                        );
                      } else {
                        await favoritesBox.put(currentApod!.date, currentApod!);
                        showCustomNotification(
                          context,
                          'Добавлено в избранное',
                        );
                      }
                      setState(() {});
                    },
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
