using System.Configuration;
using System.Data;
using System.Security.Authentication.ExtendedProtection;
using System.Windows;
using Microsoft.Extensions.DependencyInjection;

namespace Tomahawk_Space
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        public static IServiceProvider Services;

        protected override void OnStartup(StartupEventArgs e)
        {
            var services = new ServiceCollection();
            services.AddSingleton<Core>();
            Services = services.BuildServiceProvider();
            base.OnStartup(e);
        }
    }

}
