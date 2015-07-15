##Usage

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

##TODO

- Avoid garbage collection of acceleration log entries
- POST to server