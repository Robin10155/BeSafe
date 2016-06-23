function predictedMatrix=benchMarkingMFCC(model,file)

	if nargin<2
		error('function usage :-- benchMarking(<complete Path to SVMmodel>,<complete path to test file>)');
	end
	
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/');
	addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');

	load(model);

	featureIdx = [2:13 [2:13]+13];

	[completeData,fs] = wavread(file);

	durationInSec = 1;
	numberOfSamples = durationInSec * fs;

	fprintf("Number of samples in whole file: %d\n",size(completeData,1));

	previousData = zeros(0.98*fs,1);
	predictedMatrix =[];

	for i = 0:floor((size(completeData,1)/numberOfSamples))
		currentData = completeData((1+(i*numberOfSamples)):min(((i+1)*numberOfSamples),size(completeData,1)),1);
		dataToBeTested = [previousData;currentData];

		[feat,lab] = featureExtrMelCoefficients(dataToBeTested,fs,'rand',0);
		
		if(~isempty(feat))	
			feat = feat(:,featureIdx);
			testLabels = [-1];
	
			[predicted_label, accuracy, prob_estimate] = svmpredict(testLabels,feat,model,'-q');
			result_stage1 = [testLabels, predicted_label, prob_estimate];
			%if(prob_estimate >= -1)
			%	fprintf('%s, confidence level- %f\n','scream',prob_estimate);
			%else
			%	fprintf('%s, confidence level- %f\n','not-Scream',prob_estimate);
			%end
		end
		previousData = currentData(((fs-(0.98)*fs)+1):end,1);
		predictedMatrix = [predictedMatrix;predicted_label];
		disp(predicted_label);
		fflush(stdout);
	end

endfunction
