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
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;

    return Scaffold(
      backgroundColor: colorScheme.surfaceContainerLow,
      appBar: AppBar(
        title: Text(
          'Избранное',
          style: textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
        centerTitle: true,
        backgroundColor: colorScheme.surfaceContainer,
      ),
      body: ValueListenableBuilder(
        valueListenable: favoritesBox.listenable(),
        builder: (context, Box<Apod> box, _) {
          if (box.isEmpty) {
            return Center(
              child: Text(
                'Нет избранных изображений',
                style: textTheme.bodyLarge?.copyWith(
                  color: colorScheme.onSurface,
                ),
              ),
            );
          } else {
            return GridView.builder(
              padding: const EdgeInsets.all(12.0),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 4,
                crossAxisSpacing: 12.0,
                mainAxisSpacing: 12.0,
                childAspectRatio: 1.0,
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
                    cursor: SystemMouseCursors.click,
                    child: Card(
                      elevation: 2,
                      color: colorScheme.surfaceContainerLow,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12.0),
                      ),
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(12.0),
                        child: apod.mediaType == 'video'
                            ? Container(
                                color: colorScheme.surfaceContainer,
                                child: Center(
                                  child: Icon(
                                    Icons.videocam,
                                    size: 50.0,
                                    color: colorScheme.onSurfaceVariant,
                                  ),
                                ),
                              )
                            : CachedNetworkImage(
                                imageUrl: apod.url,
                                fit: BoxFit.cover,
                                placeholder: (context, url) => Center(
                                  child: CircularProgressIndicator(
                                    color: colorScheme.primary,
                                  ),
                                ),
                                errorWidget: (context, url, error) => Center(
                                  child: Icon(
                                    Icons.error,
                                    size: 50.0,
                                    color: colorScheme.error,
                                  ),
                                ),
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