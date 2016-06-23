function OptimumcgPlot(x,y,z)
	if(size(x,1) ~= 1 && size(y,1) ~= 1 && size(z,1) ~= 1)
		fprintf('%s\n', 'x,y and z should be a row vector');
		break;
	end
	x = unique(x);
	y = unique(y);
	for i= 1:length(x)
		for j = 1:length(y)
			zz(j,i)= z(j+(i-1)*length(y));
		end
	end
	figure;
	surf(x,y,zz);
	set(gca,'fontsize',20,'fontweight','bold');
	axis([-20, 50, -30,5]);
end
