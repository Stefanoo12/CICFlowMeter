package cic.cs.unb.ca.flow;

import cic.cs.unb.ca.Sys;
import cic.cs.unb.ca.jnetpcap.FlowFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FlowMgr {

    protected static final Logger logger = LoggerFactory.getLogger(FlowMgr.class);

    public static final String FLOW_SUFFIX = "_Flow.csv";

    private static FlowMgr Instance = new FlowMgr();

    private String mFlowSavePath;
    private String mDataPath;

    public List<FlowFeature> getFeatureColumns() {
        return featureColumns;
    }

    public void setFeatureColumns(List<FlowFeature> featureColumns) {
        this.featureColumns = featureColumns;
    }

    private List<FlowFeature> featureColumns = new ArrayList<>();

    private FlowMgr() {
        super();
    }
    
    public static FlowMgr getInstance() {
        return Instance;
    }

    public FlowMgr init() {

        String rootPath = System.getProperty("user.dir");
		StringBuilder sb = new StringBuilder(rootPath);
		sb.append(Sys.FILE_SEP).append("data").append(Sys.FILE_SEP);

		mDataPath = sb.toString();

        sb.append("daily").append(Sys.FILE_SEP);
        mFlowSavePath = sb.toString();

        Collections.addAll(featureColumns, FlowFeature.values());

        return Instance;
    }

    public void destroy() {
    }

	public String getSavePath() {
		return mFlowSavePath;
	}

    public String getmDataPath() {
        return mDataPath;
    }

    public String getAutoSaveFile() {
		String filename = LocalDate.now().toString()+FLOW_SUFFIX;
		return mFlowSavePath+filename;
	}
}
