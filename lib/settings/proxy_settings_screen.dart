import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ProxySettingsScreen extends StatefulWidget {
  const ProxySettingsScreen({super.key});

  @override
  State<ProxySettingsScreen> createState() => _ProxySettingsScreenState();
}

class _ProxySettingsScreenState extends State<ProxySettingsScreen> {
  final TextEditingController _proxyAddressController = TextEditingController();
  final TextEditingController _proxyPortController = TextEditingController();
  final TextEditingController _proxyUsernameController =
  TextEditingController();
  final TextEditingController _proxyPasswordController =
  TextEditingController();
  String? _proxyErrorMessage;
  bool _useProxy = false;
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  @override
  void initState() {
    super.initState();
    _loadProxySettings();
  }

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

  Future<void> _saveProxySwitch(bool value) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('use_proxy', value);
    setState(() {
      _useProxy = value;
    });
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
          'Настройки прокси',
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
                              : colorScheme.onSurface.withOpacity(0.12),
                          foregroundColor: _useProxy
                              ? colorScheme.onPrimary
                              : colorScheme.onSurface.withOpacity(0.38),
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
                                : colorScheme.onSurface.withOpacity(0.38),
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
      ),
    );
  }
}