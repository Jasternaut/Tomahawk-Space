import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';

class ApiKeySettingsScreen extends StatefulWidget {
  const ApiKeySettingsScreen({super.key});

  @override
  State<ApiKeySettingsScreen> createState() => _ApiKeySettingsScreenState();
}

class _ApiKeySettingsScreenState extends State<ApiKeySettingsScreen> {
  final TextEditingController _apiKeyController = TextEditingController();
  String? _errorMessage;
  String _currentApiKeyDescription = 'Ключ не установлен';

  @override
  void initState() {
    super.initState();
    _loadApiKey();
  }

  Future<void> _loadApiKey() async {
    final prefs = await SharedPreferences.getInstance();
    final apiKey = prefs.getString('nasa_api_key') ?? '';
    setState(() {
      _apiKeyController.text = apiKey;
      _currentApiKeyDescription = apiKey.isEmpty ? 'Ключ не установлен' : 'Ключ: $apiKey';
    });
  }

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
      _currentApiKeyDescription = apiKey;
    });
    showCustomNotification(context, 'Ключ API сохранён');
  }

  void _showApiKeyInputDialog() async {
    await showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Введите ключ API'),
          content: TextField(
            controller: _apiKeyController,
            decoration: InputDecoration(
              labelText: 'Ключ NASA API',
              border: const OutlineInputBorder(),
              errorText: _errorMessage,
              errorStyle: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.error,
                  ),
            ),
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: Theme.of(context).colorScheme.onSurface,
                ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Отмена'),
            ),
            ElevatedButton(
              onPressed: () {
                _saveApiKey();
                Navigator.pop(context);
              },
              child: const Text('Сохранить'),
            ),
          ],
        );
      },
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
          'Ключ API',
          style: textTheme.titleLarge?.copyWith(color: colorScheme.onSurface),
        ),
        elevation: 0,
        backgroundColor: colorScheme.surfaceContainerLow,
      ),
      body: Padding(
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
              child: EditableSettingsTile(
                icon: Icons.key,
                title: 'Ключ NASA API',
                description: _currentApiKeyDescription,
                onTap: _showApiKeyInputDialog,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class EditableSettingsTile extends StatelessWidget {
  final IconData icon;
  final String title;
  final String description;
  final VoidCallback onTap;

  const EditableSettingsTile({
    super.key,
    required this.icon,
    required this.title,
    required this.description,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return ListTile(
      leading: Icon(icon, color: colorScheme.primary),
      title: Text(
        title,
        style: textTheme.titleMedium?.copyWith(
          color: colorScheme.onSurface,
          fontWeight: FontWeight.bold,
        ),
      ),
      subtitle: Text(
        description,
        style: textTheme.bodyLarge?.copyWith(
          color: colorScheme.onSurfaceVariant,
        ),
      ),
      trailing: const Icon(Icons.edit, color: Colors.grey), // Иконка редактирования
      onTap: onTap,
    );
  }
}