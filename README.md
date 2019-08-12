## Overview
The Subduction library is comprised of 9 Java classes: 1 for shake detection (STFListener), 2 for image annotation (STFAnnotator, STFAnnotateActivity), 3 for persistence/delivery (STFItem, STFManager, STFRequestThread), 1 for JIRA ticket creation (STFJira), and 2 that interface with clients (STFSession, STFConfig).
 
### Design

#### Initialization/Usage

Clients must initialize an instance of STFSession in their base activity, as well as configure STF with their API token as well as to point to the correct server/JIRA project.

#### Shaking

The shake classifier samples accelerometer data that must meet the minimum thresholds for 1) acceleration and 2) window size. That is, for a "shake" event to occur, acceleration must surpass an arbitrarily chosen value and be sustained for an arbitrarily chosen length of time.

#### Annotation

The Annotator overlays the user-drawn feedback bitmap on top of the screenshot and persists the result to disk.

#### Reporting

STFItems are persisted to disk upon completion of STFAnnotateActivity. A request thread then guarantees delivery of the STF JSON object to the API server.

#### Open tickets
- Recycle acceleration samples in a pool to avoid excess garbage collection (STFListener)
- Prevent duplicate creation of STFAnnotationActivity
- Look into better ways of guaranteeing delivery that doesn't involve perpetual network requests (sad) (STFRequestThread)
- Improve exception handling


## Usage

	public abstract class BaseActivity extends Activity {
	    private STFSession stfSession;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        stfSession = STFManager.getInstance(this);
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        stfSession.onResume();
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        stfSession.onPause();
	    }
	}
