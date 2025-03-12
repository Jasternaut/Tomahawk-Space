using Tomahawk_Space;
using Tomahawk_Space.Nav;
using Tomahawk_Space.View;

namespace Tomahawk_Space
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
    
        public Home GetHome()
        {
            if (_home == null)
            {
                _home = new Home();
            }
            return _home;
        }
    
        // View folder
        private Loader _loader;

        public Loader GetLoader()
        {
            if (_loader == null)
            {
                _loader = new Loader();
            }

            return _loader;
        }
    }
}