addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load("extractedFeature.mat");
display(size(melFeatures));

%randomising data sequence
numberOfRows = size(melFeatures,1);
melFeatures = melFeatures(randperm(numberOfRows),:); 

Labels = melFeatures(:,27);
featureIdx = [2:13 [2:13]+13];
melFeatures = melFeatures(:,featureIdx);
display(size(melFeatures));

feature(1);
load("testingFeatures.mat");
display(size(testFeatures));
testFeatures = testFeatures(:,featureIdx);
display(size(testFeatures));
fflush(stdout);

datalog=[];
for log2c = -10:0.25:1
	for log2g = -7:0.25:1
		cmd = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];
		display('---------------------------------------------------------');
		fprintf('cmd =%s\n',cmd);
		display('---------------------------------------------------------');
		model = svmtrain(Labels,melFeatures,cmd);

		testLabels = [-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;1;-1;-1;-1;-1;-1;-1;-1;-1;-1;-1;1;1;1;1;-1;-1;-1;-1;-1;-1;-1;-1;1;1];

		[predicted_labels, accuracy, prob_estimates] = svmpredict(testLabels,testFeatures,model);
		result_stage1 = [testLabels, predicted_labels, prob_estimates];

		%disp(result_stage1);

		tp = sum((testLabels == 1) & (predicted_labels == 1));
		fp = sum((testLabels == -1) & (predicted_labels == 1));
		fn = sum((testLabels ==1) & (predicted_labels == -1));

		prec = tp/(tp+fp);
		rec = tp/(tp+fn);

		F1Score = (2*prec*rec)/(prec+rec);
		fprintf('tp = %f, fp = %f, fn = %f, prec= %f, rec = %f, F1 score is %f\n',tp,fp,fn,prec,rec,F1Score);
		datalog = [datalog; log2c log2g tp fp fn prec rec F1Score];
		fflush(stdout);
	end
end
display(datalog);
