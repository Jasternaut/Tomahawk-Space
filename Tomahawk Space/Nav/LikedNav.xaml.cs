using System.Windows.Controls;

namespace Tomahawk_Space.Nav;

public partial class LikedNav : Page
{
    public LikedNav()
    {
        InitializeComponent();
        Startup();
    }

    void Startup()
    {
        LikedNavDescription.Text = "В этом разделе хранятся все изображения, которые тебе понравились. " +
                                   "Показ изображений выполнен в стиле \"галерея\".";
    }
}