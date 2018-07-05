package com.hydra.test.groundtdcserver;

import com.hydra.test.groundtdcserver.AppFrame.DataBlock;
import com.hydra.test.groundtdcserver.AppFrame.ExperimentParameters;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public interface DataBlockParser {

    public abstract HashMap<String, Object> parse(DataBlock dataBlock, ExperimentParameters parameters);
}
