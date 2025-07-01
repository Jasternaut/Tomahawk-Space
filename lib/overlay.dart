import 'package:flutter/material.dart';

void showCustomNotification(BuildContext context, String message) {
  // Создаем AnimationController
  final AnimationController controller = AnimationController(
    duration: const Duration(milliseconds: 300), // Длительность анимации
    vsync: Navigator.of(context),
  );
  final Animation<Offset> offsetAnimation = Tween<Offset>(
    begin: const Offset(0.0, -1.0), // Начало: за пределами экрана сверху
    end: Offset.zero, // Конец: на своем месте
  ).animate(CurvedAnimation(
    parent: controller,
    curve: Curves.easeOut,
  ));

  // Создаем OverlayEntry
  OverlayEntry overlayEntry = OverlayEntry(
    builder: (context) => SlideTransition(
      position: offsetAnimation,
      child: SafeArea(
        child: Align(
          alignment: Alignment.topCenter, // Размещаем уведомление вверху по центру
          child: Padding(
            padding: const EdgeInsets.only(top: 50.0, left: 10.0, right: 10.0),
            child: Material(
              color: Colors.transparent,
              child: ConstrainedBox(
                constraints: const BoxConstraints(
                  maxWidth: 400.0, // Ограничиваем максимальную ширину
                  minHeight: 48.0, // Минимальная высота для текста
                ),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16.0,
                    vertical: 12.0,
                  ),
                  decoration: BoxDecoration(
                    color: Colors.black.withOpacity(0.7),
                    borderRadius: BorderRadius.circular(8.0),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.white.withOpacity(0.3), // Цвет свечения
                        blurRadius: 10.0, // Радиус свечения
                        spreadRadius: 2.0, // Распространение свечения
                        offset: const Offset(0, 0), // Без смещения
                      ),
                    ],
                  ),
                  child: Text(
                    message,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 16.0,
                      fontWeight: FontWeight.w500,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    ),
  );

  // Вставляем OverlayEntry
  Overlay.of(context).insert(overlayEntry);

  // Запускаем анимацию появления
  controller.forward();

  // Удаляем через 2 секунды с анимацией исчезновения
  Future.delayed(const Duration(seconds: 2), () async {
    await controller.reverse(); // Запускаем обратную анимацию
    overlayEntry.remove(); // Удаляем после завершения анимации
    controller.dispose(); // Очищаем контроллер
  });
}