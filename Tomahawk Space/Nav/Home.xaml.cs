using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Microsoft.Extensions.DependencyInjection;

namespace Tomahawk_Space.Nav
{
    /// <summary>
    /// Логика взаимодействия для Home.xaml
    /// </summary>
    public partial class Home : Page
    {
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

        private void LoadImage_OnClick(object sender, RoutedEventArgs e)
        {
            throw new NotImplementedException();
        }

        private void UseAdvanced_OnChecked(object sender, RoutedEventArgs e)
        {
            var appCore = App.Services.GetRequiredService<Core>();
            appCore.SetAdvancedState(true);
            UpdateState();
        }
        private void UseAdvanced_OnUnchecked(object sender, RoutedEventArgs e)
        {
            var appCore = App.Services.GetRequiredService<Core>();
            appCore.SetAdvancedState(false);
            UpdateState();
        }

        private void UpdateState()
        {
            var appCore = App.Services.GetRequiredService<Core>();
            AdvancedPanel.Visibility = appCore.GetAdvancedState() ? Visibility.Visible : Visibility.Hidden;
        }
    }
}