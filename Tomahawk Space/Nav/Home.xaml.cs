using System.Security.Policy;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using Microsoft.Extensions.DependencyInjection;
using Apod;
using System.Windows.Media.Imaging;

namespace Tomahawk_Space.Nav
{
    /// <summary>
    /// Логика взаимодействия для Home.xaml
    /// </summary>
    public partial class Home : Page
    {
        Cores.Core appCore = App.Services.GetRequiredService<Cores.Core>();
        public Home()
        {
            InitializeComponent();
            AdvancedPanel.Visibility = Visibility.Hidden;
        }


        private void NumberValidationTextBox(object sender, TextCompositionEventArgs e)
        {
            var regex = new Regex("[^0-9]+");
            e.Handled = regex.IsMatch(e.Text);
        }

        private async void LoadImage_OnClick(object sender, RoutedEventArgs e)
        {
            Console.WriteLine("Loading classes ...");
            var appLoader = App.Services.GetRequiredService<Cores.Nasa>();
            DateTime time;
            
            Console.WriteLine("Setting date ...");
            if (appCore.GetAdvancedState() == false)
            {
                time = DateTime.Now;
            }
            else
            {
                time = new DateTime(Int32.Parse(TextBoxYear.Text), Int32.Parse(TextBoxMonth.Text), Int32.Parse(TextBoxDay.Text));
            }
                
            Console.WriteLine("Fetching data ...");
            var response = await appLoader.GetClient().FetchApodAsync(time);
            var apod = response.Content;
            
            Console.WriteLine("Displaying image ...");
            var bitmapImage = new BitmapImage();
            bitmapImage.BeginInit();
            bitmapImage.UriSource = new Uri(apod.ContentUrl);
            bitmapImage.EndInit();

            appCore.GetLoader().ViewImage.Background = new ImageBrush(bitmapImage);  
            
            Console.WriteLine("Adding data to classes ...");
            appLoader.SetTitle(apod.Title);
            appLoader.SetDescription(apod.Explanation);
        }

        private void UseAdvanced_OnChecked(object sender, RoutedEventArgs e)
        {
            appCore.SetAdvancedState(true);
            UpdateState();
        }
        private void UseAdvanced_OnUnchecked(object sender, RoutedEventArgs e)
        {
            appCore.SetAdvancedState(false);
            UpdateState();
        }

        private void UpdateState()
        {
            AdvancedPanel.Visibility = appCore.GetAdvancedState() ? Visibility.Visible : Visibility.Hidden;
        }
    }
}