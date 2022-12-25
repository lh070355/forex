# Forex

- thoughts in steps
    1. Create a http client to interact with one-frame with only one pair of from & to parameters at first.
    2. Create a hash cache class, which has one element whose structure be like
        
        ```json
        {
        	"accessTime": TIMESTAMP
        	"AUD": {
        		"CAD": PRICE_AUD_CAD,
        		"CHF": PRICE_AUD_CHF,
        		...
        	},
        	"CAD": {
        		"AUD": PRICE_CAD_AUD,
        		...
        	},
        	...
        	"USD": {
        		"AUD": PRICE_USD_AUD,
        		...
        	}
        }
        ```
        
        There are 9 supported currency for forex, so there will be total 9 * 8 = 72 pairs of data need to be sent to one-frame at a time to get the latest exchange rates and then store back to this cache.
        
    3. for each of the forex request, access the cache at first, if there is no corresponding price data or the cache is expired (accessTime + 5 mins ≤ currentTime), then send API to one-frame again. This API should get the total 72 pairs of exchange rates again, and it will be like: **`GET /rates?pair=AUDCAD&pair=AUDCHF&...pair=USDSGD`** The cache strategy here is “write through”.
    4. In that way, because at most forex need to update every 5 minutes, so there will be 60 / 5 * 24 = 288 requests to one-frame at most per day.
- development  status
    - I am in the stage of building a HTTP client, and try to eliminate the compilation error when use sbt.
- notes
    - I have changed the default port of forex to 8081 to avoid conflict with docker container in 8080 port.