import 'package:flutter/material.dart';

void showCustomNotification(BuildContext context, String message) {
  final theme = Theme.of(context);
  final colorScheme = theme.colorScheme;

  final AnimationController controller = AnimationController(
    duration: const Duration(milliseconds: 300),
    vsync: Navigator.of(context),
  );
  final Animation<Offset> offsetAnimation = Tween<Offset>(
    begin: const Offset(0.0, -1.0),
    end: Offset.zero,
  ).animate(CurvedAnimation(parent: controller, curve: Curves.easeOutCubic));
  final Animation<double> opacityAnimation = Tween<double>(
    begin: 0.0,
    end: 1.0,
  ).animate(CurvedAnimation(parent: controller, curve: Curves.easeInOut));

  OverlayEntry overlayEntry = OverlayEntry(
    builder: (context) {
      return FadeTransition(
        opacity: opacityAnimation,
        child: SlideTransition(
          position: offsetAnimation,
          child: SafeArea(
            child: Align(
              alignment: Alignment.topCenter,
              child: Padding(
                padding: const EdgeInsets.only(
                  top: 50.0,
                  left: 16.0,
                  right: 16.0,
                ),
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
                        color: colorScheme.primaryContainer,
                        borderRadius: BorderRadius.circular(16.0),
                        boxShadow: [
                          BoxShadow(
                            color: colorScheme.shadow.withValues(alpha: 0.2),
                            blurRadius: 10.0,
                            spreadRadius: 1.0,
                            offset: const Offset(0, 4),
                          ),
                        ],
                      ),
                      child: Text(
                        message,
                        style: TextStyle(
                          color: colorScheme.onPrimaryContainer,
                          fontSize: 16.0,
                          fontWeight: FontWeight.w600,
                          letterSpacing: 0.5,
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
