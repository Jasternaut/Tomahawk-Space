using Tomahawk_Space.Nav;
using Tomahawk_Space.View;

namespace Tomahawk_Space.Cores
{
    public class Core
    {
        // Bool
        private bool UseAdvanced { get; set; }

        public void SetAdvancedState(bool value)
        {
            UseAdvanced = value;
        }

        public bool GetAdvancedState()
        {
            return UseAdvanced;
        }
    
        // Nav folder
        private Home _home;
        private LikedNav _liked;
    
        public Home GetHome()
        {
            if (_home == null) Console.WriteLine("Home nav is initialized");
            return _home ?? (_home = new Home());
        }

        public LikedNav GetLikedNav()
        {
            if (_liked == null) Console.WriteLine("Liked nav is initialized");
            return _liked ?? (_liked = new LikedNav());
        }
    
        // View folder
        private Loader _loader;
        private LikedView _likedView;

        public Loader GetLoader()
        {
            if (_loader == null) Console.WriteLine("Loader view is initialized");
            return _loader ?? (_loader = new Loader());
        }

        public LikedView GetLikedView()
        {
            if (_likedView == null) Console.WriteLine("Liked view is initialized");
            return _likedView ?? (_likedView = new LikedView());
        }
    }
}