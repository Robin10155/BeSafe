function singleRunMFCCSpec(data)
clear;
addpath('/home/jayraj/matlabScripts/');
addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('/home/jayraj/octaveScripts/testSVM/Mel_plus_spec_FeatureSet/bigDataSet/normTrainMFCC_and_specFeatureSet.mat');
display(size(melFeatures));
fflush(stdout);

Labels = melFeatures(:,43);
featureIdx = [2:13 [2:13]+13 27:42];
melFeatures = melFeatures(:,featureIdx);

model = svmtrain(Labels,melFeatures, '-c 256 -g 3.051757e-5');
save -V6 Normmodel8_-15.mat 'model';

feature(1);
load('testingMFCCAndSpecFeatures.mat');
display(size(testFeatures));
testFeatures = testFeatures(:,featureIdx);
display(size(testFeatures));
fflush(stdout);

testLabels = [-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;1;1];
%testLabels = [-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1];
%testLabels = [-1;-1;1;1];

[predicted_labels, accuracy, prob_estimates] = svmpredict(testLabels,testFeatures,model);
result_stage1 = [testLabels, predicted_labels, prob_estimates];
disp(result_stage1);
