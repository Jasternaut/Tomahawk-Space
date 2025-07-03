import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:tomahawk_space/theme_provider.dart';

void showCustomNotification(BuildContext context, String message) {
  final AnimationController controller = AnimationController(
    duration: const Duration(milliseconds: 300),
    vsync: Navigator.of(context),
  );
  final Animation<Offset> offsetAnimation = Tween<Offset>(
    begin: const Offset(0.0, -1.0),
    end: Offset.zero,
  ).animate(CurvedAnimation(
    parent: controller,
    curve: Curves.easeOut,
  ));

  OverlayEntry overlayEntry = OverlayEntry(
    builder: (context) {
      return Consumer<ThemeProvider>(
        builder: (context, themeProvider, child) {
          final isDarkTheme = themeProvider.isDarkTheme;
          return SlideTransition(
            position: offsetAnimation,
            child: SafeArea(
              child: Align(
                alignment: Alignment.topCenter,
                child: Padding(
                  padding: const EdgeInsets.only(top: 50.0, left: 10.0, right: 10.0),
                  child: Material(
                    color: Colors.transparent,
                    child: ConstrainedBox(
                      constraints: const BoxConstraints(
                        maxWidth: 400.0,
                        minHeight: 48.0,
                      ),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16.0,
                          vertical: 12.0,
                        ),
                        decoration: BoxDecoration(
                          color: isDarkTheme
                              ? Colors.white
                              : Colors.black,
                          borderRadius: BorderRadius.circular(50.0),
                          boxShadow: [
                            BoxShadow(
                              color: isDarkTheme
                                  ? Colors.black.withOpacity(0.3)
                                  : Colors.grey.withOpacity(0.5),
                              blurRadius: 10.0,
                              spreadRadius: 2.0,
                              offset: const Offset(0, 0),
                            ),
                          ],
                        ),
                        child: Text(
                          message,
                          style: TextStyle(
                            color: isDarkTheme
                                ? Colors.black
                                : Colors.white,
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
          );
        },
      );
    },
  );

  Overlay.of(context).insert(overlayEntry);

  controller.forward();

  Future.delayed(const Duration(seconds: 2), () async {
    await controller.reverse();
    overlayEntry.remove();
    controller.dispose();
  });
}