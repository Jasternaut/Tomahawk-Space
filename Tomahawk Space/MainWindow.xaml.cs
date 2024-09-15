using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Tomahawk_Space
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        // Перемещение окна мышкой, зажав ЛКМ на Titlebar.
        private void Titlebar_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left) 
                this.DragMove();
        }

        // Закрытие приложения кнопкой Close.
        private void Close_Click(object sender, RoutedEventArgs e)
        {
            Environment.Exit(0);
        }

        // Переход из обычного режима в полноэкранный и наоборот кнопкой MinMax.
        private void MinMax_Click(object sender, RoutedEventArgs e)
        {
            if (this.WindowState != WindowState.Maximized)
                this.WindowState = WindowState.Maximized;
            else this.WindowState = WindowState.Normal;
        }

        // Сворачивание окна кнопкой Minimize.
        private void Minimize_Click(object sender, RoutedEventArgs e)
        {
            if (this.WindowState == WindowState.Maximized | this.WindowState == WindowState.Normal)
                this.WindowState = WindowState.Minimized;
        }

        private void ShowButton_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}