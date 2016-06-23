addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load('extractedCVMelAndSpecFeature.mat');

%randomising CV data, followed by taking out feature
melFeatures = melFeatures(randperm(size(melFeatures,1)),:);
Labels = melFeatures(:,43);
featureIdx = [2:13 [2:13]+13 27:42];
melFeatures = melFeatures(:,featureIdx);
display(size(melFeatures));

%lets go for 5 slot CV
slotSize = (size(melFeatures,1))/5;
fprintf('slotSize is %d\n', slotSize);
fflush(stdout);

datalog=[];
for log2c = -1:15
	for log2g = -20:-13
		cmd = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];
		display('---------------------------------------------------------');
		fprintf('cmd =%s\n',cmd);
		display('---------------------------------------------------------');
		tpSum = 0;
		fpSum = 0;
		fnSum = 0;
		precSum = 0;
		recSum = 0;
		F1ScoreSum = 0; 
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

			tpSum = tpSum + tp;
			fpSum = fpSum + fp;
			fnSum = fnSum + fn;

			precSum = precSum + prec;
			recSum = recSum + rec;			

			F1ScoreSum = F1ScoreSum + F1Score;

			fprintf('tp = %f, fp = %f, fn = %f, prec= %f, rec = %f, F1 score is %f\n',tp,fp,fn,prec,rec,F1Score);
			fflush(stdout);
		end
		datalog = [datalog; log2c log2g tpSum fpSum fnSum precSum/5 recSum/5 F1ScoreSum/5];
	end
end
display(datalog);