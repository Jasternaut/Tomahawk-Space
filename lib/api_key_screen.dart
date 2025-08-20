import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';

class ApiKeyScreen extends StatefulWidget {
  const ApiKeyScreen({super.key});

  @override
  State<ApiKeyScreen> createState() => _ApiKeyScreenState();
}

class _ApiKeyScreenState extends State<ApiKeyScreen> {
  final TextEditingController _controller = TextEditingController();
  String? _errorMessage;

  Future<void> _saveApiKey() async {
    HapticFeedback.lightImpact();
    final apiKey = _controller.text.trim();
    if (apiKey.isEmpty) {
      setState(() {
        _errorMessage = 'Пожалуйста, введите ключ API';
      });
      return;
    }

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('nasa_api_key', apiKey);
    showCustomNotification(context, 'Ключ API сохранён');
    Navigator.pushReplacementNamed(context, '/home');
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
          'Введите ключ NASA API',
          style: textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
        centerTitle: true,
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Для загрузки изображений введите ваш ключ NASA API. Получить ключ можно на сайте api.nasa.gov.',
              textAlign: TextAlign.center,
              style: textTheme.bodyLarge?.copyWith(
                color: colorScheme.onSurface,
              ),
            ),
            const SizedBox(height: 20),
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: 'Ключ NASA API',
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
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _saveApiKey,
              style: ElevatedButton.styleFrom(
                backgroundColor: colorScheme.primary,
                foregroundColor: colorScheme.onPrimary,
                padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 24.0),
              ),
              child: Text(
                'Сохранить и продолжить',
                style: textTheme.labelLarge?.copyWith(
                  color: colorScheme.onPrimary,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}