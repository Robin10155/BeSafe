addpath('/home/jayraj/octaveScripts/')
addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('/home/jayraj/octaveScripts/testSVM/MelFeatureSets/big_set/train/TrainBigMFCCFeatureSet.mat');
display(size(melFeatures));
Labels = melFeatures(:,27);
featureIdx = [2:13 [2:13]+13];
melFeatures = melFeatures(:,featureIdx);

log2c = 12;
log2g = -24;
str = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];

model = svmtrain(Labels,melFeatures, str);
save -v6 model12_-24.mat 'model'

featureMFCC(1);
load('testingFeatures.mat');
testFeatures = testFeatures(:,featureIdx);

testLabels = -1*ones(size(testFeatures,1),1);

[predicted_labels, accuracy, prob_estimates] = svmpredict(testLabels,testFeatures,model);
result_stage1 = [testLabels,predicted_labels, prob_estimates];
disp(predicted_labels);
