clear;
addpath('/home/jayraj/octaveScripts/')
addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('extractedMelAndSpecFeatureInOneGo.mat');
display(size(melFeatures));
%fflush(stdout);
Labels = melFeatures(:,43);
featureIdx = [2:13 [2:13]+13 27:42];
melFeatures = melFeatures(:,featureIdx);

model = svmtrain(Labels,melFeatures, '-c 256 -g 3.051757e-5');
save -V6 octavemodel.mat 'model';

feature(1);
load('testingMFCCAndSpecFeatures.mat');
%load('stage_sobb_scream_2.mat')
display(size(testFeatures));
%featureIdx = [2:13 [2:13]+39 118:133];
testFeatures = testFeatures(:,featureIdx);
narrowedMatlabData = testFeatures;
save -V6 matlabData.mat 'narrowedMatlabData';
display(size(testFeatures));
%fflush(stdout);

testLabels = -1*ones(size(testFeatures,1),1);

[predicted_labels, accuracy, prob_estimates] = svmpredict(testLabels,testFeatures,model);
result_stage1 = [testLabels, predicted_labels, prob_estimates];
disp(result_stage1);
