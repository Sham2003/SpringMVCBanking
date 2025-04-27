package com.banking.ml;


//import com.banking.dto.loan.LoanFeatures;
//import org.springframework.stereotype.Component;
//
//@Component
//public class LoanApprover {
//    public String approveLoan(LoanFeatures features) {
//        System.out.println(features.toString());
//        System.out.println("Approving loan");
//        return "Approved";
//    }
//}

import com.banking.dto.loan.LoanFeatures;
import org.springframework.stereotype.Component;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Component
public class LoanApprover {

    private final OrtEnvironment env;
    private final OrtSession session;

    public LoanApprover() throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession("src/main/resources/ml/loan_model.onnx", new OrtSession.SessionOptions());
        System.out.println(session.getInputNames());
        session.getInputInfo().forEach((k,v) -> System.out.println(k + " → " + v.toString()));
    }

    public String approveLoan(LoanFeatures features) throws OrtException {
        Map<String, OnnxTensor> inputs = new HashMap<>();

        // Prepare each input separately based on model input names
        Map<String, Float> featureMap = features.toFeatureMap();
        for (String inputName : session.getInputNames()) {
            Float value = featureMap.get(inputName);
            OnnxTensor tensor;

            if (value != null) {
                tensor = OnnxTensor.createTensor(env, new float[][]{{value}});
            } else {
                throw new IllegalArgumentException("Unsupported feature type for " + inputName);
            }

            inputs.put(inputName, tensor);
        }

        OrtSession.Result result = session.run(inputs);
        System.out.println("============================");
        System.out.println(result.size());
        System.out.println(result.get(0).getType());

        // 4. Extract output
        String[] output = (String[]) result.get(0).getValue();
        System.out.println(Arrays.toString(output));
        return output[0];
    }
}