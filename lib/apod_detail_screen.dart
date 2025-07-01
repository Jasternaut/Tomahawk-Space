import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:cached_network_image/cached_network_image.dart';

class ApodDetailScreen extends StatelessWidget {
  final Apod apod;

  const ApodDetailScreen({super.key, required this.apod});

  @override
  Widget build(BuildContext context) {
    final box = Hive.box<Apod>('favorites');
    return Scaffold(
      appBar: AppBar(
        title: Text(apod.title),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete),
            onPressed: () async {
              await box.delete(apod.date);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Удалено из избранного')),
              );
              Navigator.pop(context);
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: LayoutBuilder(
          builder: (context, constraints) {
            if (constraints.maxWidth > 900) {
              return Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Expanded(
                    flex: 1,
                    child: apod.mediaType == 'video'
                        ? Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const Text(
                                'Это видео, а не изображение. Откройте по ссылке:',
                                textAlign: TextAlign.center,
                              ),
                              const SizedBox(height: 8),
                              InkWell(
                                onTap: () async {
                                  final Uri uri = Uri.parse(apod.url);
                                  if (await canLaunchUrl(uri)) {
                                    await launchUrl(uri);
                                  } else {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(
                                        content: Text(
                                          'Не удалось открыть ссылку: ${apod.url}',
                                        ),
                                      ),
                                    );
                                  }
                                },
                                child: Text(
                                  apod.url,
                                  textAlign: TextAlign.center,
                                  style: const TextStyle(
                                    color: Colors.blue,
                                    decoration: TextDecoration.underline,
                                  ),
                                ),
                              ),
                            ],
                          )
                        : CachedNetworkImage(
                            imageUrl: apod.url,
                            fit: BoxFit.contain,
                            placeholder: (context, url) => const Center(
                              child: CircularProgressIndicator(),
                            ),
                            errorWidget: (context, url, error) => const Center(
                              child: Text('Не удалось загрузить изображение.'),
                            ),
                          ),
                  ),
                  Expanded(
                    flex: 1,
                    child: Padding(
                      padding: const EdgeInsets.all(8.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            apod.title,
                            style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 16),
                          Text(
                            apod.explanation,
                            textAlign: TextAlign.justify,
                            style: const TextStyle(fontSize: 16),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'Дата: ${apod.date}',
                            style: const TextStyle(
                              fontSize: 14,
                              fontStyle: FontStyle.italic,
                              color: Colors.grey,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              );
            } else {
              return Column(
                children: [
                  Text(
                    apod.title,
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 16),
                  apod.mediaType == 'video'
                      ? Column(
                          children: [
                            const Text(
                              'Это видео, а не изображение. Откройте по ссылке:',
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 8),
                            InkWell(
                              onTap: () async {
                                final Uri uri = Uri.parse(apod.url);
                                if (await canLaunchUrl(uri)) {
                                  await launchUrl(uri);
                                } else {
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    SnackBar(
                                      content: Text(
                                        'Не удалось открыть ссылку: ${apod.url}',
                                      ),
                                    ),
                                  );
                                }
                              },
                              child: Text(
                                apod.url,
                                textAlign: TextAlign.center,
                                style: const TextStyle(
                                  color: Colors.blue,
                                  decoration: TextDecoration.underline,
                                ),
                              ),
                            ),
                          ],
                        )
                      : CachedNetworkImage(
                          imageUrl: apod.url,
                          fit: BoxFit.contain,
                          placeholder: (context, url) =>
                              const Center(child: CircularProgressIndicator()),
                          errorWidget: (context, url, error) => const Center(
                            child: Text('Не удалось загрузить изображение.'),
                          ),
                        ),
                  const SizedBox(height: 16),
                  Text(
                    apod.explanation,
                    textAlign: TextAlign.justify,
                    style: const TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Дата: ${apod.date}',
                    style: const TextStyle(
                      fontSize: 14,
                      fontStyle: FontStyle.italic,
                      color: Colors.grey,
                    ),
                  ),
                ],
              );
            }
          },
        ),
      ),
    );
  }
}
