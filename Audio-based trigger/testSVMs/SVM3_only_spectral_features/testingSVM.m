addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load("extractedFeature.mat");
display(size(melFeatures));
Labels = melFeatures(:,17);
melFeatures = melFeatures(:,1:16);

%training SVM with default settings 
melFeatures = melFeatures .- repmat(mean(melFeatures,1),size(melFeatures,1),1);
model = svmtrain(Labels,melFeatures);

feature(1);
load("testingFeatures.mat");
display(size(testFeatures));
testFeatures = testFeatures(:,1:16);
display(size(testFeatures));

%if training is normalised we need to normalise tes data also.
testFeatures = testFeatures .- repmat(mean(testFeatures,1),size(testFeatures,1),1);
svmpredict(zeros(size(testFeatures,1),1),testFeatures,model)