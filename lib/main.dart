import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:tomahawk_space/home_screen.dart';
import 'package:tomahawk_space/favorites_screen.dart';
import 'package:tomahawk_space/settings_screen.dart';
import 'package:tomahawk_space/api_key_screen.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:tomahawk_space/theme_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Hive.initFlutter();
  Hive.registerAdapter(ApodAdapter());
  await Hive.openBox<Apod>('favorites');
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  Future<bool> _hasApiKey() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.containsKey('nasa_api_key');
  }

  Future<int> _getInitialTabIndex() async {
    final prefs = await SharedPreferences.getInstance();
    final isFirstLaunch = prefs.getBool('isFirstLaunch') ?? true;
    if (isFirstLaunch) {
      await prefs.setBool('isFirstLaunch', false);
      await prefs.setInt('selected_tab', 0); // Вкладка "Главная" для первого запуска
      return 0;
    }
    return prefs.getInt('selected_tab') ?? 0;
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => ThemeProvider(),
      child: Consumer<ThemeProvider>(
        builder: (context, themeProvider, child) {
          return MaterialApp(
            title: 'Tomahawk Space',
            theme: ThemeData(
              useMaterial3: true, // Включаем Material 3
              colorScheme: ColorScheme.fromSeed(
                seedColor: const Color.fromARGB(255, 146, 39, 196), // const Color.fromARGB(255, 243, 192, 80),
                brightness: themeProvider.isDarkTheme ? Brightness.dark : Brightness.light,
              ),
            ),
            localizationsDelegates: const [
              DefaultMaterialLocalizations.delegate,
              DefaultWidgetsLocalizations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            supportedLocales: const [
              Locale('en', ''),
              Locale('ru', ''),
            ],
            home: FutureBuilder<bool>(
              future: _hasApiKey(),
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (snapshot.hasData && snapshot.data == true) {
                  return FutureBuilder<int>(
                    future: _getInitialTabIndex(),
                    builder: (context, indexSnapshot) {
                      if (indexSnapshot.connectionState == ConnectionState.waiting) {
                        return const Center(child: CircularProgressIndicator());
                      }
                      return MyHomePage(initialIndex: indexSnapshot.data ?? 0);
                    },
                  );
                }
                return const ApiKeyScreen();
              },
            ),
            routes: {
              '/home': (context) => const MyHomePage(),
            },
          );
        },
      ),
    );
  }
}

class MyHomePage extends StatefulWidget {
  final int initialIndex;
  const MyHomePage({super.key, this.initialIndex = 0});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late int _selectedIndex;

  static const List<Widget> _widgetOptions = <Widget>[
    HomeScreen(),
    FavoritesScreen(),
    SettingsScreen(),
  ];

  static const List<NavigationDestination> _navDestinations = [
    NavigationDestination(
      icon: Icon(Icons.home),
      label: 'Главная',
    ),
    NavigationDestination(
      icon: Icon(Icons.favorite),
      label: 'Избранное',
    ),
    NavigationDestination(
      icon: Icon(Icons.settings),
      label: 'Настройки',
    ),
  ];

  @override
  void initState() {
    super.initState();
    _selectedIndex = widget.initialIndex;
  }

  Future<void> _saveSelectedIndex(int index) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt('selected_tab', index);
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
      _saveSelectedIndex(index);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, child) {
        return Scaffold(
          body: _widgetOptions.elementAt(_selectedIndex),
          bottomNavigationBar: NavigationBar(
            selectedIndex: _selectedIndex,
            onDestinationSelected: _onItemTapped,
            destinations: _navDestinations,
            backgroundColor: themeProvider.isDarkTheme
                ? Colors.grey[900]
                : Colors.white,
            indicatorColor: themeProvider.isDarkTheme
                ? const Color.fromARGB(255, 140, 57, 153)
                : const Color.fromARGB(255, 235, 142, 250),
            labelBehavior: NavigationDestinationLabelBehavior.alwaysShow,
            elevation: 4, // Добавляет тень для современного вида
          ),
        );
      },
    );
  }
}