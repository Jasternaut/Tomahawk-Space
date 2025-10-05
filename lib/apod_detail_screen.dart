import 'package:flutter/material.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:tomahawk_space/models/apod.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:tomahawk_space/overlay.dart';
import 'package:flutter/services.dart';

class ApodDetailScreen extends StatelessWidget {
  final Apod apod;

  const ApodDetailScreen({super.key, required this.apod});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    final textTheme = theme.textTheme;
    final box = Hive.box<Apod>('favorites');

    return Scaffold(
      backgroundColor: colorScheme.surfaceContainerLow,
      appBar: AppBar(
        title: Text(
          apod.title,
          style: textTheme.titleLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
        backgroundColor: colorScheme.surfaceContainer,
        actions: [
          IconButton(
            icon: Icon(
              Icons.delete,
              color: colorScheme.error,
            ),
            onPressed: () async {
              HapticFeedback.lightImpact();
              await box.delete(apod.date);
              showCustomNotification(context, 'Удалено из избранного');
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
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    flex: 1,
                    child: apod.mediaType == 'video'
                        ? Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'Это видео, а не изображение. Откройте по ссылке:',
                                textAlign: TextAlign.center,
                                style: textTheme.bodyLarge?.copyWith(
                                  color: colorScheme.onSurface,
                                ),
                              ),
                              const SizedBox(height: 8),
                              InkWell(
                                onTap: () async {
                                  final Uri uri = Uri.parse(apod.url);
                                  if (await canLaunchUrl(uri)) {
                                    await launchUrl(uri);
                                  } else {
                                    showCustomNotification(
                                      context,
                                      'Не удалось открыть ссылку: ${apod.url}',
                                    );
                                  }
                                },
                                child: Text(
                                  apod.url,
                                  textAlign: TextAlign.center,
                                  style: textTheme.bodyLarge?.copyWith(
                                    color: colorScheme.primary,
                                    decoration: TextDecoration.underline,
                                  ),
                                ),
                              ),
                            ],
                          )
                        : CachedNetworkImage(
                            imageUrl: apod.url,
                            imageBuilder: (context, imageProvider) => Container(
                                            decoration: BoxDecoration(
                                              image: DecorationImage(
                                                image: imageProvider,
                                                fit: BoxFit.cover,
                                              ),
                                              borderRadius: BorderRadius.circular(12),
                                            ),
                                            height: 400,
                                          ),
                            fit: BoxFit.contain,
                            placeholder: (context, url) => Center(
                              child: CircularProgressIndicator(
                                color: colorScheme.primary,
                              ),
                            ),
                            errorWidget: (context, url, error) => Center(
                              child: Text(
                                'Не удалось загрузить изображение.',
                                style: textTheme.bodyLarge?.copyWith(
                                  color: colorScheme.error,
                                ),
                              ),
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
                          const SizedBox(height: 16),
                          Text(
                            apod.explanation,
                            textAlign: TextAlign.justify,
                            style: textTheme.bodyLarge?.copyWith(
                              color: colorScheme.onSurface,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            'Дата: ${apod.date}',
                            style: textTheme.bodyMedium?.copyWith(
                              color: colorScheme.onSurfaceVariant,
                              fontStyle: FontStyle.italic,
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
                  const SizedBox(height: 16),
                  apod.mediaType == 'video'
                      ? Column(
                          children: [
                            Text(
                              'Это видео, а не изображение. Откройте по ссылке:',
                              textAlign: TextAlign.center,
                              style: textTheme.bodyLarge?.copyWith(
                                color: colorScheme.onSurface,
                              ),
                            ),
                            const SizedBox(height: 8),
                            InkWell(
                              onTap: () async {
                                final Uri uri = Uri.parse(apod.url);
                                if (await canLaunchUrl(uri)) {
                                  await launchUrl(uri);
                                } else {
                                  showCustomNotification(
                                    context,
                                    'Не удалось открыть ссылку: ${apod.url}',
                                  );
                                }
                              },
                              child: Text(
                                apod.url,
                                textAlign: TextAlign.center,
                                style: textTheme.bodyLarge?.copyWith(
                                  color: colorScheme.primary,
                                  decoration: TextDecoration.underline,
                                ),
                              ),
                            ),
                          ],
                        )
                      : CachedNetworkImage(
                          imageUrl: apod.url,
                          imageBuilder: (context, imageProvider) => Container(
                                            decoration: BoxDecoration(
                                              image: DecorationImage(
                                                image: imageProvider,
                                                fit: BoxFit.cover,
                                              ),
                                              borderRadius: BorderRadius.circular(12),
                                            ),
                                            height: 400,
                                          ),
                          fit: BoxFit.contain,
                          placeholder: (context, url) => Center(
                            child: CircularProgressIndicator(
                              color: colorScheme.primary,
                            ),
                          ),
                          errorWidget: (context, url, error) => Center(
                            child: Text(
                              'Не удалось загрузить изображение.',
                              style: textTheme.bodyLarge?.copyWith(
                                color: colorScheme.error,
                              ),
                            ),
                          ),
                        ),
                  const SizedBox(height: 16),
                  Text(
                    apod.explanation,
                    textAlign: TextAlign.justify,
                    style: textTheme.bodyLarge?.copyWith(
                      color: colorScheme.onSurface,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Дата: ${apod.date}',
                    style: textTheme.bodyMedium?.copyWith(
                      color: colorScheme.onSurfaceVariant,
                      fontStyle: FontStyle.italic,
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