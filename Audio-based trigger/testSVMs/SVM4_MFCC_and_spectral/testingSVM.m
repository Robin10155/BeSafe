addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load("extractedFeature.mat");
display(size(melFeatures));
Labels = melFeatures(:,41);
melFeatures = melFeatures(:,1:40);

%training SVM with default settings 
model = svmtrain(Labels,melFeatures,'-c 512 -g 0.00390')
display('-c 512 -g 0.00390');
fflush(stdout);

feature(1);
load("testingFeatures.mat");
feature_index = [2:13 [2:13]+13 27:43];
testFeatures = testFeatures(:,feature_index);
display(size(testFeatures));
testFeatures = testFeatures(:,1:40);
display(size(testFeatures));
fflush(stdout);

[predicted_labels, accuracy, prob_estimates] = svmpredict([-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1],testFeatures,model);
result_stage1 = [[-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1], predicted_labels, prob_estimates];
disp(result_stage1);
