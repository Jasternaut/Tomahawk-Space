import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  static final ValueNotifier<Color> selectedColor = ValueNotifier<Color>(
    const Color.fromARGB(255, 146, 39, 196),
  );
  static final ValueNotifier<bool> useDynamicTheme = ValueNotifier<bool>(true);
  static final ValueNotifier<Brightness> selectedBrightness =
      ValueNotifier<Brightness>(Brightness.light);

  static Future<void> loadSelectedColor() async {
    final prefs = await SharedPreferences.getInstance();
    final colorValue =
        prefs.getInt('selected_color') ??
        const Color.fromARGB(255, 146, 39, 196).toARGB32(); // value
    selectedColor.value = Color(colorValue);
  }

  static Future<void> loadDynamicThemePreference() async {
    final prefs = await SharedPreferences.getInstance();
    useDynamicTheme.value = prefs.getBool('use_dynamic_theme') ?? true;
  }

  static Future<void> loadBrightnessPreference() async {
    final prefs = await SharedPreferences.getInstance();
    final brightnessString = prefs.getString('theme_brightness') ?? 'light';
    selectedBrightness.value = brightnessString == 'light'
        ? Brightness.light
        : Brightness.dark;
  }

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> with AutomaticKeepAliveClientMixin {

  @override
  bool get wantKeepAlive => true;

  final TextEditingController _apiKeyController = TextEditingController();
  final TextEditingController _proxyAddressController = TextEditingController();
  final TextEditingController _proxyPortController = TextEditingController();
  final TextEditingController _proxyUsernameController =
      TextEditingController();
  final TextEditingController _proxyPasswordController =
      TextEditingController();
  String? _errorMessage;
  String? _proxyErrorMessage;
  bool _useProxy = false;
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  // Предустановленные цвета для светлой и тёмной тем
  final List<Map<String, Color>> _lightColorOptions = [
    {
      'primary': const Color.fromARGB(255, 146, 39, 196),
      'secondary': const Color.fromARGB(255, 181, 83, 226),
      'tertiary': const Color.fromARGB(255, 95, 21, 129),
    },
    {
      'primary': const Color.fromARGB(255, 91, 40, 209),
      'secondary': const Color.fromARGB(255, 120, 80, 220),
      'tertiary': const Color.fromARGB(255, 70, 30, 150),
    },
    {
      'primary': const Color.fromARGB(255, 59, 92, 184),
      'secondary': const Color.fromARGB(255, 80, 120, 200),
      'tertiary': const Color.fromARGB(255, 40, 70, 140),
    },
    {
      'primary': const Color.fromARGB(255, 33, 47, 90),
      'secondary': const Color.fromARGB(255, 50, 70, 120),
      'tertiary': const Color.fromARGB(255, 20, 30, 60),
    },
  ];

  final List<Map<String, Color>> _darkColorOptions = [
    {
      'primary': const Color.fromARGB(255, 200, 100, 255),
      'secondary': const Color.fromARGB(255, 220, 150, 255),
      'tertiary': const Color.fromARGB(255, 150, 80, 200),
    },
    {
      'primary': const Color.fromARGB(255, 140, 80, 255),
      'secondary': const Color.fromARGB(255, 160, 100, 255),
      'tertiary': const Color.fromARGB(255, 100, 60, 200),
    },
    {
      'primary': const Color.fromARGB(255, 100, 120, 220),
      'secondary': const Color.fromARGB(255, 120, 140, 240),
      'tertiary': const Color.fromARGB(255, 80, 100, 180),
    },
    {
      'primary': const Color.fromARGB(255, 50, 70, 120),
      'secondary': const Color.fromARGB(255, 70, 90, 140),
      'tertiary': const Color.fromARGB(255, 30, 50, 100),
    },
  ];

  @override
  void initState() {
    super.initState();
    _loadApiKey();
    _loadProxySettings();
  }

  // Загрузка ключа API из SharedPreferences
  Future<void> _loadApiKey() async {
    final prefs = await SharedPreferences.getInstance();
    final apiKey = prefs.getString('nasa_api_key') ?? '';
    setState(() {
      _apiKeyController.text = apiKey;
    });
  }

  // Загрузка настроек прокси из SharedPreferences
  Future<void> _loadProxySettings() async {
    final prefs = await SharedPreferences.getInstance();
    final password = await _secureStorage.read(key: 'proxy_password') ?? '';
    setState(() {
      _useProxy = prefs.getBool('use_proxy') ?? false;
      _proxyAddressController.text = prefs.getString('proxy_address') ?? '';
      _proxyPortController.text = prefs.getString('proxy_port') ?? '';
      _proxyUsernameController.text = prefs.getString('proxy_username') ?? '';
      _proxyPasswordController.text = password;
    });
  }

  // Сохранение ключа API в SharedPreferences
  Future<void> _saveApiKey() async {
    HapticFeedback.lightImpact();
    final apiKey = _apiKeyController.text.trim();
    if (apiKey.isEmpty) {
      setState(() {
        _errorMessage = 'Пожалуйста, введите ключ API';
      });
      return;
    }

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('nasa_api_key', apiKey);
    setState(() {
      _errorMessage = null;
    });
    showCustomNotification(context, 'Ключ API сохранён');
  }

  // Сохранение настроек прокси в SharedPreferences
  Future<void> _saveProxySettings() async {
    HapticFeedback.lightImpact();
    final address = _proxyAddressController.text.trim();
    final port = _proxyPortController.text.trim();
    final username = _proxyUsernameController.text.trim();
    final password = _proxyPasswordController.text.trim();

    if (_useProxy && (address.isEmpty || port.isEmpty)) {
      setState(() {
        _proxyErrorMessage =
            'Адрес и порт обязательны при использовании прокси';
      });
      return;
    }

    if (_useProxy && port.isNotEmpty) {
      final portNumber = int.tryParse(port);
      if (portNumber == null || portNumber < 1 || portNumber > 65535) {
        setState(() {
          _proxyErrorMessage = 'Порт должен быть числом от 1 до 65535';
        });
        return;
      }
    }

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('use_proxy', _useProxy);
    await prefs.setString('proxy_address', address);
    await prefs.setString('proxy_port', port);
    await prefs.setString('proxy_username', username);
    await _secureStorage.write(key: 'proxy_password', value: password);
    setState(() {
      _proxyErrorMessage = null;
    });
    showCustomNotification(context, 'Настройки прокси сохранены');
  }

  // Сохранение переключателя прокси
  Future<void> _saveProxySwitch(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('use_proxy', value);
    setState(() {
      _useProxy = value;
    });
  }

  // Сохранение настроек прокси в SharedPreferences
  Future<void> _saveSelectedColor(Color color, Brightness brightness) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt('selected_color', color.toARGB32()); // value
    await prefs.setString(
      'theme_brightness',
      brightness == Brightness.light ? 'light' : 'dark',
    );
    setState(() {
      SettingsScreen.selectedColor.value = color;
      SettingsScreen.selectedBrightness.value = brightness;
    });
    showCustomNotification(context, 'Цвет и тема изменены');
  }

  // Сохранение переключателя динамической темы
  Future<void> _saveDynamicThemePreference(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('use_dynamic_theme', value);
    setState(() {
      SettingsScreen.useDynamicTheme.value = value;
    });
    showCustomNotification(
      context,
      value ? 'Динамическая тема включена' : 'Динамическая тема отключена',
    );
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
        title: Text(
          'Настройки',
          style: textTheme.titleLarge?.copyWith(color: colorScheme.onSurface),
        ),
        centerTitle: true,
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          // Раздел "Внешний вид"
          Card(
            elevation: 2,
            color: colorScheme.surfaceContainer,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12.0),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ListTile(
                    leading: Icon(Icons.color_lens, color: colorScheme.primary),
                    title: Text(
                      'Внешний вид',
                      style: textTheme.titleMedium?.copyWith(
                        color: colorScheme.onSurface,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  ListTile(
                    title: Text(
                      'Использовать динамическую тему',
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                    ),
                    trailing: ValueListenableBuilder<bool>(
                      valueListenable: SettingsScreen.useDynamicTheme,
                      builder: (context, useDynamicTheme, child) {
                        return Switch(
                          value: useDynamicTheme,
                          onChanged: (bool newValue) {
                            HapticFeedback.lightImpact();
                            _saveDynamicThemePreference(newValue);
                          },
                          activeThumbColor: colorScheme.onInverseSurface,
                          activeTrackColor: colorScheme.onSurfaceVariant
                        );
                      },
                    ),
                  ),
                  ValueListenableBuilder<bool>(
                    valueListenable: SettingsScreen.useDynamicTheme,
                    builder: (context, useDynamicTheme, child) {
                      if (!useDynamicTheme) {
                        return ValueListenableBuilder<Brightness>(
                          valueListenable: SettingsScreen.selectedBrightness,
                          builder: (context, selectedBrightness, child) {
                            return Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Padding(
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 16.0,
                                  ),
                                  child: Text(
                                    'Светлые темы:',
                                    style: textTheme.titleMedium?.copyWith(
                                      color: colorScheme.onSurface,
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                                const SizedBox(height: 16),
                                Center(
                                  child: Wrap(
                                    spacing: 16,
                                    runSpacing: 16,
                                    children: _lightColorOptions
                                        .asMap()
                                        .entries
                                        .map((entry) {
                                          final colors = entry.value;
                                          return _buildColorCircle(
                                            context,
                                            colors['primary']!,
                                            colors['secondary']!,
                                            colors['tertiary']!,
                                            Brightness.light,
                                            selectedBrightness,
                                          );
                                        })
                                        .toList(),
                                  ),
                                ),
                                const SizedBox(height: 16),
                                Padding(
                                  padding: const EdgeInsets.symmetric(
                                    horizontal: 16.0,
                                  ),
                                  child: Text(
                                    'Тёмные темы:',
                                    style: textTheme.titleMedium?.copyWith(
                                      color: colorScheme.onSurface,
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                                const SizedBox(height: 16),
                                Center(
                                  child: Wrap(
                                    spacing: 16,
                                    runSpacing: 16,
                                    children: _darkColorOptions
                                        .asMap()
                                        .entries
                                        .map((entry) {
                                          final colors = entry.value;
                                          return _buildColorCircle(
                                            context,
                                            colors['primary']!,
                                            colors['secondary']!,
                                            colors['tertiary']!,
                                            Brightness.dark,
                                            selectedBrightness,
                                          );
                                        })
                                        .toList(),
                                  ),
                                ),
                              ],
                            );
                          },
                        );
                      }
                      return const SizedBox.shrink();
                    },
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          // Раздел "Ключ NASA"
          Card(
            elevation: 2,
            color: colorScheme.surfaceContainer,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12.0),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ListTile(
                    leading: Icon(Icons.key, color: colorScheme.primary),
                    title: Text(
                      'Ключ NASA',
                      style: textTheme.titleMedium?.copyWith(
                        color: colorScheme.onSurface,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: TextField(
                      controller: _apiKeyController,
                      decoration: InputDecoration(
                        labelText: 'Изменить ключ NASA API',
                        border: const OutlineInputBorder(),
                        errorText: _errorMessage,
                        errorStyle: textTheme.bodyMedium?.copyWith(
                          color: colorScheme.error,
                        ),
                        labelStyle: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurface,
                        ),
                      ),
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                    ),
                  ),
                  const SizedBox(height: 20),
                  Center(
                    child: ElevatedButton(
                      onPressed: _saveApiKey,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: colorScheme.primary,
                        foregroundColor: colorScheme.onPrimary,
                        padding: const EdgeInsets.symmetric(
                          vertical: 16.0,
                          horizontal: 32.0,
                        ),
                      ),
                      child: Text(
                        'Сохранить ключ',
                        style: textTheme.labelLarge?.copyWith(
                          color: colorScheme.onPrimary,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),
          // Раздел "Настройки прокси"
          Card(
            elevation: 2,
            color: colorScheme.surfaceContainer,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12.0),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  ListTile(
                    leading: Icon(
                      Icons.network_check,
                      color: colorScheme.primary,
                    ),
                    title: Text(
                      'Настройки прокси',
                      style: textTheme.titleMedium?.copyWith(
                        color: colorScheme.onSurface,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  ListTile(
                    title: Text(
                      'Использовать прокси',
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                    ),
                    trailing: Switch(
                      value: _useProxy,
                      onChanged: (bool newValue) {
                        HapticFeedback.lightImpact();
                        _saveProxySwitch(newValue);
                      },
                      activeThumbColor: colorScheme.onInverseSurface,
                      activeTrackColor: colorScheme.onSurfaceVariant,
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: TextField(
                      controller: _proxyAddressController,
                      decoration: InputDecoration(
                        labelText: 'Адрес прокси',
                        border: const OutlineInputBorder(),
                        errorText: _proxyErrorMessage,
                        errorStyle: textTheme.bodyMedium?.copyWith(
                          color: colorScheme.error,
                        ),
                        labelStyle: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurface,
                        ),
                      ),
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                      enabled: _useProxy,
                    ),
                  ),
                  const SizedBox(height: 16),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: TextField(
                      controller: _proxyPortController,
                      decoration: InputDecoration(
                        labelText: 'Порт прокси',
                        border: const OutlineInputBorder(),
                        errorStyle: textTheme.bodyMedium?.copyWith(
                          color: colorScheme.error,
                        ),
                        labelStyle: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurface,
                        ),
                      ),
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                      keyboardType: TextInputType.number,
                      enabled: _useProxy,
                    ),
                  ),
                  const SizedBox(height: 16),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: TextField(
                      controller: _proxyUsernameController,
                      decoration: InputDecoration(
                        labelText: 'Логин (необязательно)',
                        border: const OutlineInputBorder(),
                        errorStyle: textTheme.bodyMedium?.copyWith(
                          color: colorScheme.error,
                        ),
                        labelStyle: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurface,
                        ),
                      ),
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                      enabled: _useProxy,
                    ),
                  ),
                  const SizedBox(height: 16),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: TextField(
                      controller: _proxyPasswordController,
                      decoration: InputDecoration(
                        labelText: 'Пароль (необязательно)',
                        border: const OutlineInputBorder(),
                        errorStyle: textTheme.bodyMedium?.copyWith(
                          color: colorScheme.error,
                        ),
                        labelStyle: textTheme.bodyLarge?.copyWith(
                          color: colorScheme.onSurface,
                        ),
                      ),
                      style: textTheme.bodyLarge?.copyWith(
                        color: colorScheme.onSurface,
                      ),
                      obscureText: true,
                      enabled: _useProxy,
                    ),
                  ),
                  const SizedBox(height: 20),
                  Center(
                    child: ElevatedButton(
                      onPressed: _useProxy ? _saveProxySettings : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _useProxy
                            ? colorScheme.primary
                            : colorScheme.onSurface.withValues(alpha: 0.12),
                        foregroundColor: _useProxy
                            ? colorScheme.onPrimary
                            : colorScheme.onSurface.withValues(alpha: 0.38),
                        padding: const EdgeInsets.symmetric(
                          vertical: 16.0,
                          horizontal: 32.0,
                        ),
                      ),
                      child: Text(
                        'Сохранить настройки прокси',
                        style: textTheme.labelLarge?.copyWith(
                          color: _useProxy
                              ? colorScheme.onPrimary
                              : colorScheme.onSurface.withValues(alpha: 0.38),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildColorCircle(
    BuildContext context,
    Color primary,
    Color secondary,
    Color tertiary,
    Brightness brightness,
    Brightness selectedBrightness,
  ) {
    final colorScheme = Theme.of(context).colorScheme;
    return GestureDetector(
      onTap: () => _saveSelectedColor(primary, brightness),
      child: Container(
        width: 60,
        height: 60,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          border: Border.all(
            color: colorScheme.onSurface,
            width:
                SettingsScreen.selectedColor.value == primary &&
                    SettingsScreen.selectedBrightness.value == brightness
                ? 3
                : 1,
          ),
        ),
        child: ClipOval(
          child: Column(
            children: [
              Expanded(child: Container(color: primary)),
              Expanded(
                child: Row(
                  children: [
                    Expanded(child: Container(color: secondary)),
                    Expanded(child: Container(color: tertiary)),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
