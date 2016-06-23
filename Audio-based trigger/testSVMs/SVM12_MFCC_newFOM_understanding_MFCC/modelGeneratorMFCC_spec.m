function modelGeneratorMFCC_spec(typeOfModel,log2cmin,log2cmax,log2gmin,log2gmax)

	addpath('/home/jayraj/octaveScripts/');
	addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');

	if nargin<5
		error('function usage-modelGenerator(<typeOfModel-"withnorm"/"withoutnorm"> ,<log2cmin>,<log2cmax>,<log2gmin>,<log2gmax>)');
	end

	if strcmp(typeOfModel,'withnorm')
		load('/home/jayraj/octaveScripts/testSVM/Mel_plus_spec_FeatureSet/bigDataSet/normTrainMFCC_and_specFeatureSet.mat');	
	end	
	if strcmp(typeOfModel,'withoutnorm')
		load('/home/jayraj/octaveScripts/testSVM/Mel_plus_spec_FeatureSet/bigDataSet/trainMFCC_and_specFeatureSet.mat');
	end
	
	Labels = melFeatures(:,43);
	featureIdx = [2:13 [2:13]+13 27:42];
	melFeatures = melFeatures(:,featureIdx);
	
	for log2c = log2cmin:log2cmax
		for log2g = log2gmin:log2gmax
			cmd = ['-c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];
			fprintf('cmd =%s\n',cmd);
			fflush(stdout);
			model = svmtrain(Labels,melFeatures,cmd);
			
			if strcmp(typeOfModel,'withnorm')
				str = ['/home/jayraj/octaveScripts/testSVM/models_MFCC_spec/withnorm/Normmodel',num2str(log2c),'_',num2str(log2g),'.mat'];
				save('-V6',str,'model');	
			end
			if strcmp(typeOfModel,'withoutnorm')
				str = ['/home/jayraj/octaveScripts/testSVM/models_MFCC_spec/withoutnorm/model',num2str(log2c),'_',num2str(log2g),'.mat'];
				save('-V6',str,'model');
			end
		end
	end
end
