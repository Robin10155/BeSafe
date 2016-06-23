function [melFeatures,melLabels] = melCoefficients(filePath,type,vectorClass);
files = dir(fullfile(filePath,type));
fprintf('Number of .wav file being processed: %d\n',length(files));
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
