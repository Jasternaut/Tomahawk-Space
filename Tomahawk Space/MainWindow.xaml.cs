using System.Resources;
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
using Tomahawk_Space.Properties;

namespace Tomahawk_Space
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        string maximize = "\uE922";
        string restore = "\uE923";
        bool is_maximized = false;

        double w_height {  get; set; }
        double w_width {  get; set; }
        double w_top {  get; set; }
        double w_left {  get; set; }

        public MainWindow()
        {
            InitializeComponent();
            UpdateWindowData(this.Width, this.Height, this.Top, this.Left);
        }

        // Перемещение окна мышкой, зажав ЛКМ на Titlebar.
        private void Titlebar_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left) 
                this.DragMove();
        }

        private void CloseButton_Click(object sender, RoutedEventArgs e)
        {
            Environment.Exit(0);
        }

        void UpdateWindowData(double width, double height, double top, double left)
        {
            w_height = height;
            w_width = width;
            w_top = top;
            w_left = left;
        }

        private void MinMaxButton_Click(object sender, RoutedEventArgs e)
        {
            var screen = System.Windows.SystemParameters.WorkArea;

            switch (is_maximized)
            {
                case false:
                    UpdateWindowData(this.Width, this.Height, this.Top, this.Left);

                    this.Top = screen.Top;
                    this.Left = screen.Left;
                    this.Width = screen.Width;
                    this.Height = screen.Height;
                    MinMaxButton.Content = "";
                    MinMaxButton.Content = restore;
                    is_maximized = true;
                    break;
                case true:
                    this.Top = w_top;
                    this.Left = w_left;
                    this.Width = w_width;
                    this.Height = w_height;
                    MinMaxButton.Content = "";
                    MinMaxButton.Content = maximize;
                    is_maximized = false;
                    break;
            }
        }

        private void MinimizeButton_Click(object sender, RoutedEventArgs e)
        {
            
            
            switch (this.WindowState)
            {
                case WindowState.Normal:
                    this.WindowState = WindowState.Minimized;
                    break;
                case WindowState.Maximized:
                    this.WindowState = WindowState.Minimized;
                    break;
            }
        }

        private void HomeButton_Click(object sender, RoutedEventArgs e)
        {

        }

        private void LikedButton_Click(object sender, RoutedEventArgs e)
        {

        }

        private void SettingsButton_Click(object sender, RoutedEventArgs e)
        {

        }
    }
}