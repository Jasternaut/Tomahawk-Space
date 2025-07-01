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
            return GridView.builder(
              padding: const EdgeInsets.all(12.0),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 4, // Количество столбцов
                crossAxisSpacing: 12.0, // Отступ между столбцами
                mainAxisSpacing: 12.0, // Отступ между строками
                childAspectRatio: 1.0, // Соотношение сторон (квадратные элементы)
              ),
              itemCount: box.length,
              itemBuilder: (context, index) {
                final apod = box.getAt(index)!;
                return GestureDetector(
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => ApodDetailScreen(apod: apod),
                      ),
                    );
                  },
                  child: MouseRegion(
                    cursor: SystemMouseCursors.click, // Курсор указателя при наведении
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(12.0), // Закругление углов
                      child: apod.mediaType == 'video'
                          ? Container(
                              color: Colors.grey[300],
                              child: const Center(
                                child: Icon(
                                  Icons.videocam,
                                  size: 50.0,
                                  color: Colors.black54,
                                ),
                              ),
                            )
                          : CachedNetworkImage(
                              imageUrl: apod.url,
                              fit: BoxFit.cover, // Заполнить всю область
                              placeholder: (context, url) => const Center(
                                child: CircularProgressIndicator(),
                              ),
                              errorWidget: (context, url, error) => const Center(
                                child: Icon(Icons.error, size: 50.0),
                              ),
                            ),
                    ),
                  ),
                );
              },
            );
          }
        },
      ),
    );
  }
}