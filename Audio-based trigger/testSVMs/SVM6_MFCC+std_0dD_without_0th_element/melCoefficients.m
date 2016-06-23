function [melFeatures,melLabels] = melCoefficients(filePath,type,vectorClass);
files = dir(fullfile(filePath,type));
disp('Number of .wav file being processed:');
disp(length(files));
fflush(stdout);
melFeatures=[];
melLabels=[];
for i = 1:length(files)
	filename = files(i).name;
	%display(filename);
	[audio,fs]=wavread([filePath,filename]);
	
	[feat,lab] = featureExtrMelCoefficients(audio,fs,filename(1:end-4),vectorClass);

	melFeatures = [melFeatures; feat];
	melLabels = [melLabels, lab];
end
