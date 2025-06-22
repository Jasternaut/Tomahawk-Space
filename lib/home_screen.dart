import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:cached_network_image/cached_network_image.dart';

Future<Apod> fetchApod({String? date}) async {
  final prefs = await SharedPreferences.getInstance();
  final String apiKey = prefs.getString('nasa_api_key') ?? 'DEMO_KEY'; // Запасной ключ на случай ошибки
  String url = 'https://api.nasa.gov/planetary/apod?api_key=$apiKey';

  if (date != null && date.isNotEmpty) {
    url += '&date=$date';
  }

  final response = await http.get(Uri.parse(url));
  if (response.statusCode == 200) {
    return Apod.fromJson(jsonDecode(response.body));
  } else {
    throw Exception('Не удалось загрузить изображение дня: ${response.statusCode}');
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  late Future<Apod>? futureApod;
  String? _selectedDate;
  Apod? currentApod; // Для хранения текущего изображения

  bool _isDateSelectionEnabled = false;
  final TextEditingController _dateController = TextEditingController();
  final Box<Apod> favoritesBox = Hive.box<Apod>('favorites');

  @override
  void initState() {
    super.initState();
    _loadApod();
  }

  @override
  void dispose() {
    _dateController.dispose();
    super.dispose();
  }

  Future<void> _loadApod() async {
    setState(() {
      futureApod = fetchApod(date: _selectedDate).then((apod) {
        setState(() {
          currentApod = apod; // Обновляем currentApod после загрузки
        });
        return apod;
      }).catchError((error) {
        setState(() {
          currentApod = null; // Сбрасываем при ошибке
        });
        throw error;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('NASA APOD'),
        centerTitle: true,
        actions: [
          if (currentApod != null)
            IconButton(
              icon: Icon(
                favoritesBox.containsKey(currentApod!.date)
                    ? Icons.favorite
                    : Icons.favorite_border,
              ),
              onPressed: () async {
                if (favoritesBox.containsKey(currentApod!.date)) {
                  await favoritesBox.delete(currentApod!.date);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Удалено из избранного')),
                  );
                } else {
                  await favoritesBox.put(currentApod!.date, currentApod!);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Добавлено в избранное')),
                  );
                }
                setState(() {});
              },
            ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: TextButton(
              onPressed: _loadApod,
              child: const Text('Загрузить', style: TextStyle(color: Colors.grey)),
            ),
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Checkbox(
                      value: _isDateSelectionEnabled,
                      onChanged: (bool? newValue) {
                        setState(() {
                          _isDateSelectionEnabled = newValue ?? false;
                          if (!_isDateSelectionEnabled) {
                            _selectedDate = null;
                            _dateController.clear();
                          }
                        });
                      },
                    ),
                    const Text('Указать конкретную дату'),
                  ],
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
                          );
                          if (pickedDate != null) {
                            setState(() {
                              _selectedDate =
                                  "${pickedDate.year}-${pickedDate.month.toString().padLeft(2, '0')}-${pickedDate.day.toString().padLeft(2, '0')}";
                              _dateController.text = _selectedDate!;
                            });
                          }
                        },
                        child: AbsorbPointer(
                          child: TextFormField(
                            controller: _dateController,
                            decoration: const InputDecoration(
                              labelText: 'Дата (ГГГГ-ММ-ДД)',
                              border: OutlineInputBorder(),
                              suffixIcon: Icon(Icons.calendar_today),
                              enabled: false,
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                const SizedBox(height: 16),
              ],
            ),
          ),
          Expanded(
            child: Center(
              child: FutureBuilder<Apod>(
                future: futureApod,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const CircularProgressIndicator();
                  } else if (snapshot.hasError) {
                    return Text('Ошибка: ${snapshot.error}');
                  } else if (snapshot.hasData) {
                    final apod = snapshot.data!;
                    return SingleChildScrollView(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Text(
                            apod.title,
                            textAlign: TextAlign.center,
                            style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 16),
                          apod.mediaType == 'video'
                              ? Column(
                                  children: [
                                    const Text(
                                      'Это видео, а не изображение. Откройте по ссылке:',
                                      textAlign: TextAlign.center,
                                    ),
                                    const SizedBox(height: 8),
                                    InkWell(
                                      onTap: () async {
                                        final Uri uri = Uri.parse(apod.url);
                                        if (await canLaunchUrl(uri)) {
                                          await launchUrl(uri);
                                        } else {
                                          ScaffoldMessenger.of(context).showSnackBar(
                                            SnackBar(
                                                content: Text(
                                                    'Не удалось открыть ссылку: ${apod.url}')),
                                          );
                                        }
                                      },
                                      child: Text(
                                        apod.url,
                                        textAlign: TextAlign.center,
                                        style: const TextStyle(
                                          color: Colors.blue,
                                          decoration: TextDecoration.underline,
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              : 
                                CachedNetworkImage(
                                  imageUrl: apod.url,
                                  placeholder: (context, url) => const CircularProgressIndicator(),
                                  errorWidget: (context, url, error) => const Icon(Icons.error),
                                ),
                          const SizedBox(height: 16),
                          Text(
                            apod.explanation,
                            textAlign: TextAlign.justify,
                            style: const TextStyle(fontSize: 16),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'Дата: ${apod.date}',
                            style: const TextStyle(
                              fontSize: 14,
                              fontStyle: FontStyle.italic,
                              color: Colors.grey,
                            ),
                          ),
                        ],
                      ),
                    );
                  } else {
                    return const Text('Нажмите кнопку "Обновить" для загрузки изображения дня.');
                  }
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}