using System.Windows;
using System.Windows.Input;
using Microsoft.Extensions.DependencyInjection;

namespace Tomahawk_Space
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        //Core AppCore = new Core();
        private const string Maximize = "\uE922";
        private const string Restore = "\uE923";
        private bool _isMaximized = true;
        private bool _isDragMove = false;

        private double WHeight {  get; set; }
        private double WWidth {  get; set; }
        private double WTop {  get; set; }
        private double WLeft {  get; set; }

        public MainWindow()
        {
            InitializeComponent();
            UpdateWindowData(this.Width, this.Height, this.Top, this.Left);
            var appCore = App.Services.GetRequiredService<Cores.Core>();
            NavFrame.Navigate(appCore.GetHome());
            ViewFrame.Navigate(appCore.GetLoader());
        }

        // Перемещение окна мышкой, зажав ЛКМ на Titlebar.
        private void Titlebar_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left && _isMaximized == false)
            {
                _isMaximized = true;
                _isDragMove = true;
                UpdateWindowState();
                DragMove();
                _isDragMove = false;
            }
            else if (e.ChangedButton == MouseButton.Left && _isMaximized == true)
            {
                DragMove();
            }
        }

        private void CloseButton_Click(object sender, RoutedEventArgs e)
        {
            Environment.Exit(0);
        }

        private void UpdateWindowState()
        {
            var screen = SystemParameters.WorkArea;

            switch (_isMaximized)
            {
                case false:
                    UpdateWindowData(this.Width, this.Height, this.Top, this.Left);

                    this.Top = screen.Top;
                    this.Left = screen.Left;
                    this.Width = screen.Width;
                    this.Height = screen.Height;
                    this.ResizeMode = ResizeMode.NoResize;
                    MinMaxButton.Content = "";
                    MinMaxButton.Content = Restore;
                    
                    break;
                case true:
                    if (!_isDragMove)
                    {
                        this.Top = WTop;
                        this.Left = WLeft;
                    }
                    this.Width = WWidth;
                    this.Height = WHeight;
                    this.ResizeMode = ResizeMode.CanResize;
                    MinMaxButton.Content = "";
                    MinMaxButton.Content = Maximize;
                    
                    break;
            }
        }
        
        private void UpdateWindowData(double width, double height, double top, double left)
        {
            WHeight = height;
            WWidth = width;
            WTop = top;
            WLeft = left;
        }

        private void MinMaxButton_Click(object sender, RoutedEventArgs e)
        {
            _isMaximized = !_isMaximized;
            UpdateWindowState();
        }

        private void MinimizeButton_Click(object sender, RoutedEventArgs e)
        {
            this.WindowState = this.WindowState switch
            {
                WindowState.Normal => WindowState.Minimized,
                WindowState.Maximized => WindowState.Minimized,
                _ => this.WindowState
            };
        }
        
        // NavFrame - панель управления
        // ViewFrame - для показа
        private void HomeButton_Click(object sender, RoutedEventArgs e)
        {
            var appCore = App.Services.GetRequiredService<Cores.Core>();
            NavFrame.Navigate(appCore.GetHome());
            ViewFrame.Navigate(appCore.GetLoader());
        }

        private void LikedButton_Click(object sender, RoutedEventArgs e)
        {
            var appCore = App.Services.GetRequiredService<Cores.Core>();
            NavFrame.Navigate(appCore.GetLikedNav());
            ViewFrame.Navigate(appCore.GetLikedView());
        }

        private void SettingsButton_Click(object sender, RoutedEventArgs e)
        {
            //var appCore = App.Services.GetRequiredService<Cores.Core>();
        }
    }
}