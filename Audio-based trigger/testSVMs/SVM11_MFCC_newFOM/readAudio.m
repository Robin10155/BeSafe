fid = fopen('pipe','r','ieee-le')

% later change mode of file to append
%writeID = fopen('recording.out','a','ieee-le')
writeID = fopen('outputPipe','w','ieee-le')

disp(fid);
disp(writeID);
fflush(stdout);

fs = 44100;
numberOfSamples = 1.98*fs;

featureIdx = [2:13 [2:13]+13];
load('Normmodel15_14.mat');

addpath('../../');
addpath('/home/jayraj/octaveScripts/voicebox/');
addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');

i=1;
val= [];

while (i<=500)
	val = fread(fid,numberOfSamples,"int16");
	fprintf('\n');
	format long E;
	disp(time());
	wavFormatData = val./(2^15);

	[feat,lab] = featureExtrMelCoefficients(wavFormatData,fs,'rand',0);
	
	if(~isempty(feat))	

		feat = feat(:,featureIdx);
	
		testLabels = [-1];
	
		[predicted_label, accuracy, prob_estimate] = svmpredict(testLabels,feat,model);
		result_stage1 = [testLabels, predicted_label, prob_estimate];
		fprintf('%f\n',prob_estimate);
		if(prob_estimate >= -1)
			%sending raw data to opuputPipe
			fprintf('%s\n','scream')
			fwrite(writeID,val,"int16");
		else
			fprintf('%s\n','non-scream');
		end
		format long E;
		disp(time());
		fflush(stdout);
	end

	i=i+1;
end
fclose(fid);
fclose(writeID);
