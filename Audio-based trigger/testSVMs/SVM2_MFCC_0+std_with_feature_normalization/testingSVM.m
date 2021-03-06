addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load("extractedFeature.mat");
display(size(melFeatures));
Labels = melFeatures(:,27);
melFeatures = melFeatures(:,2:26);

%training SVM with default settings 
%normalizing melFeature before training
%melFeatures = melFeatures .- repmat(mean(melFeatures,1),size(melFeatures,1),1);
%melFeatures = melFeatures ./ (repmat(max(melFeatures),size(melFeatures,1),1));
model = svmtrain(Labels,melFeatures, '-c 0.3 -g 0.038');

feature(1);
load("testingFeatures.mat");
display(size(testFeatures));
testFeatures = testFeatures(:,2:26);
display(size(testFeatures));
fflush(stdout);

%if training is normalised we need to normalise tes data also.
%testFeatures = testFeatures .- repmat(mean(testFeatures,1),size(testFeatures,1),1);
%testFeatures = testFeatures ./ (repmat(max(testFeatures),size(testFeatures,1),1)); 
[predicted_labels, accuracy, prob_estimates] = svmpredict([-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1],testFeatures,model);
result_stage1 = [[-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1], predicted_labels, prob_estimates];
disp(result_stage1);
