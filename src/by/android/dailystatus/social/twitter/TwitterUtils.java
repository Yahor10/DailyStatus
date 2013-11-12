package by.android.dailystatus.social.twitter;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final  class TwitterUtils {
	
	private RequestToken requestToken = null;
    private TwitterFactory twitterFactory = null;
    private Twitter twitter;

    public TwitterUtils() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(ConstantValues.TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(ConstantValues.TWITTER_CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitterFactory = new TwitterFactory(configuration);
        twitter = twitterFactory.getInstance();
    }
    
    public TwitterFactory getTwitterFactory()
    {
        return twitterFactory;
    }
	
    public void setTwitterFactory(AccessToken accessToken)
    {
        twitter = twitterFactory.getInstance(accessToken);
    }
    
    public Twitter getTwitter()
    {
        return twitter;
    }
    
    public RequestToken getRequestToken() {
        if (requestToken == null) {
            try {
                requestToken = twitterFactory.getInstance().getOAuthRequestToken(ConstantValues.TWITTER_CALLBACK_URL);
            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return requestToken;
    }
    
    static TwitterUtils instance = new TwitterUtils();

    public static TwitterUtils getInstance() {
        return instance;
    }


    public void reset() {
        instance = new TwitterUtils();
    }
    
    
////////////////////////////////////////////////////////////////////////////////////////	
	
	public static Twitter isAuthenticated(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");

		if (token == null || token.length() == 0 || secret == null
				|| secret.length() == 0)
			return null;

		try {
			AccessToken a = new AccessToken(token, secret);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(TwitterConstants.CONSUMER_KEY,
					TwitterConstants.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(a);
			return twitter;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static void saveAccessToken(SharedPreferences prefs, AccessToken a) {
		final Editor edit = prefs.edit();
		edit.putString(OAuth.OAUTH_TOKEN, a.getToken());
		edit.putString(OAuth.OAUTH_TOKEN_SECRET, a.getTokenSecret());
		edit.commit();
	}

}
