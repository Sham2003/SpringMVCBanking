package com.banking.ml;


import com.banking.dto.loan.LoanFeatures;
import org.springframework.stereotype.Component;

@Component
public class LoanApprover {
    public String approveLoan(LoanFeatures features) {
        System.out.println(features.toString());
        System.out.println("Approving loan");
        return "Approved";
    }
}
//
//import org.springframework.stereotype.Component;
//import ai.onnxruntime.OrtEnvironment;
//import ai.onnxruntime.OrtSession;
//import ai.onnxruntime.OnnxTensor;
//import ai.onnxruntime.ORTException;
//
//
//
//
//@Component
//public class LoanApprover {
//
//    private final OrtEnvironment env;
//    private final OrtSession session;
//
//    public LoanApprover() throws OrtException, IOException {
//        env = OrtEnvironment.getEnvironment();
//        session = env.createSession("src/main/resources/ml/loan_model.onnx", new OrtSession.SessionOptions());
//    }
//
//    public String approveLoan(LoanFeatures features) {
//        try {
//            // 1. Prepare input
//            float[] inputData = features.toFloatArray(); // We'll define this
//            long[] shape = new long[]{1, inputData.length}; // [batch_size, features]
//
//            OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData, shape);
//
//            // 2. Prepare input map
//            Map<String, OnnxTensor> inputs = new HashMap<>();
//            inputs.put(session.getInputNames().iterator().next(), inputTensor); // usually only one input
//
//            // 3. Run inference
//            OrtSession.Result result = session.run(inputs);
//
//            // 4. Extract output
//            float[][] output = (float[][]) result.get(0).getValue();
//            if (output[0][0] >= 0.5) {
//                return "Approval";
//            } else {
//                return "Rejected";
//            }
//
//
//        } catch (Exception e) {
//            throw new RuntimeException("ONNX scoring failed", e);
//        }
//    }
//}