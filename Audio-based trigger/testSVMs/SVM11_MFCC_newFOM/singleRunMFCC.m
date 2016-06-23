addpath('/home/jayraj/octaveScripts/')
addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('/home/jayraj/octaveScripts/testSVM/MelFeatureSets/not-normalized/TrainMFCCFeature.mat');
melFeatures = melFeatures(randperm(size(melFeatures,1)),:);
display(size(melFeatures));
Labels = melFeatures(:,27);
display(size(Labels));
featureIdx = [2:13 [2:13]+13];
melFeatures = melFeatures(:,featureIdx);
display(size(melFeatures));

log2c = 15;
log2g = -14;
str = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];

model = svmtrain(Labels,melFeatures, str);
save -V6 model15_14.mat 'model';

featureMFCC(1);
load('testingFeatures.mat');
display(size(testFeatures));
testFeatures = testFeatures(:,featureIdx);
display(size(testFeatures));

testLabels = -1*ones(size(testFeatures,1),1);

[predicted_labels, accuracy, prob_estimates] = svmpredict(testLabels,testFeatures,model);
result_stage1 = [testLabels, predicted_labels, prob_estimates];
disp(result_stage1);
