import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:tomahawk_space/apod_detail_screen.dart';
import 'package:cached_network_image/cached_network_image.dart';

class FavoritesScreen extends StatefulWidget {
  const FavoritesScreen({super.key});

  @override
  State<FavoritesScreen> createState() => _FavoritesScreenState();
}

class _FavoritesScreenState extends State<FavoritesScreen> {
  late Box<Apod> favoritesBox;

  @override
  void initState() {
    super.initState();
    favoritesBox = Hive.box<Apod>('favorites');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Избранное'),
        centerTitle: true,
      ),
      body: ValueListenableBuilder(
        valueListenable: favoritesBox.listenable(),
        builder: (context, Box<Apod> box, _) {
          if (box.isEmpty) {
            return const Center(
              child: Text('Нет избранных изображений'),
            );
          } else {
            return ListView.builder(
              itemCount: box.length,
              itemBuilder: (context, index) {
                final apod = box.getAt(index)!;
                return ListTile(
                  title: Text(apod.title),
                  subtitle: Text(apod.date),
                  leading: CachedNetworkImage(
                      imageUrl: apod.url,
                      width: 50,
                      height: 50,
                      fit: BoxFit.cover,
                      errorWidget: (context, url, error) => const Icon(Icons.error),
                    ),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ApodDetailScreen(apod: apod),
                      ),
                    );
                  },
                );
              },
            );
          }
        },
      ),
    );
  }
}