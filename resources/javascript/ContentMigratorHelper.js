if (ContentMigratorHelper == null) var ContentMigratorHelper = {};

ContentMigratorHelper.exportRepository = function(output) {
	ContentMigrator.doExport(output, {
		callback: function(result) {
			closeAllLoadingMessages();
			
			if (result != null)
				alert(result.value);
		}
	});
}

ContentMigratorHelper.importRepository = function(input) {
	ContentMigrator.doImport(input, {
		callback: function(result) {
			closeAllLoadingMessages();
			
			if (result != null)
				alert(result.value);
		}
	});
}


ContentMigratorHelper.migrate = function(path) {
	jQuery('input.contentMigratorButton').attr('disabled', 'disabled');
	
	var intervalId = null;
	jQuery('ul.failuresList').html('');
	ContentMigrator.doMigrate(path, {
		callback: function(result) {
			closeAllLoadingMessages();
			
			jQuery('div.migrationProgress').hide('fast');
			jQuery('input.contentMigratorButton').removeAttr('disabled');
			
			if (intervalId != null)
				window.clearInterval(intervalId);
				
			if (result != null)
				alert(result.value);
		}
	});
	
	intervalId = window.setInterval(function() {
		ContentMigrator.getMigrationProgress({
			callback: function(progress) {
				if (progress != null) {
					closeAllLoadingMessages();
					
					jQuery('div.migrationProgress').removeAttr('style');
					jQuery('#migrationProgress').html(progress.progress);
					
					if (progress.failures != null && progress.failures.length > 0) {
						jQuery('#failures').removeAttr('style');
						for (var i = 0; i < progress.failures.length; i++)
							jQuery('ul.failuresList').append('<li>' + progress.failures[i] + '</li>');

						ContentMigrator.doClearMigrationFailures();
					}
				}
			},
			errorHandler: function(ob1, ob2) {
			}
		});
	}, 3000);
}