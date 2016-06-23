addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('CVMFCCFeatureSet.mat');

%randomising CV data, followed by taking out feature
melFeatures = melFeatures(randperm(size(melFeatures,1)),:);
Labels = melFeatures(:,27);
featureIdx = [2:13 [2:13]+13];
melFeatures = melFeatures(:,featureIdx);
display(size(melFeatures));

%lets go for 5 slot CV
slotSize = round((size(melFeatures,1))/5);
fprintf('slotSize is %d\n', slotSize);
fflush(stdout);

datalog=[];
for log2c = -3:6
	for log2g = -8:-1
		cmd = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];
		display('---------------------------------------------------------');
		fprintf('cmd =%s\n',cmd);
		display('---------------------------------------------------------');
		for i = 0:4
			CVtestIndex = [(1+(slotSize*i)) : (slotSize+(slotSize*i))];
			CVtestLabels = Labels(CVtestIndex);
			CVtestData = melFeatures(CVtestIndex,:);
			%fprintf('Size of CVtestLabels is: %d %d\n', size(CVtestLabels));
			%fprintf('Size of CVtestData is: %d %d\n', size(CVtestData));
			fflush(stdout);
			
			trainLabels = Labels;
			trainLabels(CVtestIndex) = [];
			trainData = melFeatures;
			trainData(CVtestIndex,:) = [];
			%fprintf('Size of trainLabels is: %d %d\n', size(trainLabels));
			%fprintf('Size of trainData is: %d %d\n', size(trainData));
			fflush(stdout);
					
			model = svmtrain(trainLabels,trainData,cmd);
 
			[predicted_labels, accuracy, prob_estimates] = svmpredict(CVtestLabels,CVtestData,model);
			result_stage1 = [CVtestLabels, predicted_labels, prob_estimates];

			%disp(result_stage1);

			tp = sum((CVtestLabels == 1) & (predicted_labels == 1));
			fp = sum((CVtestLabels == -1) & (predicted_labels == 1));
			fn = sum((CVtestLabels ==1) & (predicted_labels == -1));

			prec = tp/(tp+fp);
			rec = tp/(tp+fn);

			F1Score = (2*prec*rec)/(prec+rec);
			fprintf('tp = %f, fp = %f, fn = %f, prec= %f, rec = %f, F1 score is %f\n',tp,fp,fn,prec,rec,F1Score);
			datalog = [datalog; log2c log2g tp fp fn prec rec F1Score];
			fflush(stdout);
		end
	end
end
display(datalog);
