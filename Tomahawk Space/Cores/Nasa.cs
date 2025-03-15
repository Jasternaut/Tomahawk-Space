using Apod;

namespace Tomahawk_Space.Cores
{
    public class Nasa
    {
        private ApodClient _client;
        private string _title;
        private string _description;

        public ApodClient GetClient()
        {
            return _client ?? (_client = new ApodClient("kWsUfUrcmOd5oWWxxc8KzTvPeRqRqjNYfc1in4c0"));
        }

        public void SetTitle(string title)
        {
            _title = title;
        }

        public void SetDescription(string description)
        {
            _description = description;
        }
    
        public string GetTitle(){return _title;}
        public string GetDescription(){return _description;}
    }
}