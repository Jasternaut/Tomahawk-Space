import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/settings/theme_settings_screen.dart';
import 'package:tomahawk_space/settings/api_key_settings_screen.dart';
import 'package:tomahawk_space/settings/proxy_settings_screen.dart';

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
            const Color.fromARGB(255, 146, 39, 196).value;
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
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: ListView(
        children: [
          ListTile(
            leading: Icon(Icons.palette, color: colorScheme.primary),
            title: Text('Тема и внешний вид'),
            subtitle: Text('Настройка цвета, динамической темы и яркости'),
            trailing: Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const ThemeSettingsScreen()),
              );
            },
          ),
          ListTile(
            leading: Icon(Icons.key, color: colorScheme.primary),
            title: Text('Ключ API'),
            subtitle: Text('Ввод и сохранение ключа NASA API'),
            trailing: Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const ApiKeySettingsScreen()),
              );
            },
          ),
          ListTile(
            leading: Icon(Icons.network_check, color: colorScheme.primary),
            title: Text('Настройки прокси'),
            subtitle: Text('Включение прокси, адрес, порт и аутентификация'),
            trailing: Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const ProxySettingsScreen()),
              );
            },
          ),
        ],
      ),
    );
  }
}

extension ColorExtension on Color {
  int toARGB32() {
    return value;
  }
}