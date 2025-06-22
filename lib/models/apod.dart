// lib/models/apod.dart
import 'package:hive/hive.dart';

part 'apod.g.dart';

@HiveType(typeId: 0)
class Apod extends HiveObject {
  @HiveField(0)
  final String title;

  @HiveField(1)
  final String explanation;

  @HiveField(2)
  final String url;

  @HiveField(3)
  final String? hdurl;

  @HiveField(4)
  final String date;

  @HiveField(5)
  final String? mediaType;

  Apod({
    required this.title,
    required this.explanation,
    required this.url,
    this.hdurl,
    required this.date,
    this.mediaType,
  });

  factory Apod.fromJson(Map<String, dynamic> json) {
    return Apod(
      title: json['title'] as String,
      explanation: json['explanation'] as String,
      url: json['url'] as String,
      hdurl: json['hdurl'] as String?,
      date: json['date'] as String,
      mediaType: json['media_type'] as String?,
    );
  }
}