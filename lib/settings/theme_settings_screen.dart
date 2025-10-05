import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';
import 'package:tomahawk_space/settings_screen.dart';

class ThemeSettingsScreen extends StatefulWidget {
  const ThemeSettingsScreen({super.key});

  @override
  State<ThemeSettingsScreen> createState() => _ThemeSettingsScreenState();
}

class _ThemeSettingsScreenState extends State<ThemeSettingsScreen> {
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

  Future<void> _saveSelectedColor(Color color, Brightness brightness) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt('selected_color', color.value);
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

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return Scaffold(
      backgroundColor: colorScheme.surfaceContainerLow,
      appBar: AppBar(
        title: Text(
          'Тема и внешний вид',
          style: textTheme.titleLarge?.copyWith(color: colorScheme.onSurface),
        ),
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
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
                        Icons.color_lens,
                        color: colorScheme.primary,
                      ),
                      title: Text(
                        'Тема и цвета',
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
                      trailing: ValueListenableBuilder(
                        valueListenable: SettingsScreen.useDynamicTheme,
                        builder: (context, useDynamic, _) {
                          return Switch(
                            value: useDynamic,
                            onChanged: (bool newValue) {
                              HapticFeedback.lightImpact();
                              _saveDynamicThemePreference(newValue);
                            },
                            activeThumbColor: colorScheme.onInverseSurface,
                            activeTrackColor: colorScheme.onSurfaceVariant,
                          );
                        },
                      ),
                    ),
                    ValueListenableBuilder(
                      valueListenable: SettingsScreen.useDynamicTheme,
                      builder: (context, useDynamic, _) {
                        if (useDynamic) {
                          return const SizedBox.shrink();
                        }
                        return Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 16.0),
                              child: Text(
                                'Светлая тема',
                                style: textTheme.titleMedium?.copyWith(
                                  color: colorScheme.onSurface,
                                ),
                              ),
                            ),
                            const SizedBox(height: 8),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 16.0),
                              child: Wrap(
                                spacing: 16.0,
                                runSpacing: 16.0,
                                children: _lightColorOptions.map((option) {
                                  return ValueListenableBuilder(
                                    valueListenable: SettingsScreen.selectedBrightness,
                                    builder: (context, selectedBrightness, _) {
                                      return _buildColorCircle(
                                        context,
                                        option['primary']!,
                                        option['secondary']!,
                                        option['tertiary']!,
                                        Brightness.light,
                                        selectedBrightness,
                                      );
                                    },
                                  );
                                }).toList(),
                              ),
                            ),
                            const SizedBox(height: 24),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 16.0),
                              child: Text(
                                'Тёмная тема',
                                style: textTheme.titleMedium?.copyWith(
                                  color: colorScheme.onSurface,
                                ),
                              ),
                            ),
                            const SizedBox(height: 8),
                            Padding(
                              padding: const EdgeInsets.symmetric(horizontal: 16.0),
                              child: Wrap(
                                spacing: 16.0,
                                runSpacing: 16.0,
                                children: _darkColorOptions.map((option) {
                                  return ValueListenableBuilder(
                                    valueListenable: SettingsScreen.selectedBrightness,
                                    builder: (context, selectedBrightness, _) {
                                      return _buildColorCircle(
                                        context,
                                        option['primary']!,
                                        option['secondary']!,
                                        option['tertiary']!,
                                        Brightness.dark,
                                        selectedBrightness,
                                      );
                                    },
                                  );
                                }).toList(),
                              ),
                            ),
                          ],
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}