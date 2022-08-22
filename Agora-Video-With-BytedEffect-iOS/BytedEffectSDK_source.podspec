Pod::Spec.new do |s|
  s.name         = "BytedEffectSDK_source"
  s.version      = "4.0.3.0"
  s.license      = {
  :type => 'Proprietary',
  :text => <<-LICENSE
  BytedEffects. All Rights Reserved.
  LICENSE
  }
  s.homepage     = 'https://github.com'
  s.authors      = 'lab-cv'
  s.summary      = 'Demo for effect-sdk'
  s.description  = <<-DESC
  * Demo for effect-sdk
  DESC


  s.frameworks   = 'Accelerate','AssetsLibrary','AVFoundation','CoreGraphics','CoreImage','CoreMedia','CoreVideo','Foundation','QuartzCore','UIKit'
  s.weak_frameworks = 'Metal','MetalPerformanceShaders','Photos', 'CoreML'
  s.source       = { :git => "./", :tag => s.version.to_s }
  s.source_files  =  ["../../../../../effect_sdk/inc/ai_tob/*.h","../../../../../effect_sdk/inc/ai_tob/lens/*.h","../../../../../effect_sdk/AILib/licbag_sdk/*.h"]
  s.public_header_files = ["../../../../../effect_sdk/inc/ai_tob/*.h","../../../../../effect_sdk/inc/ai_tob/lens/*.h","../../../../../effect_sdk/AILib/licbag_sdk/*.h"]
  s.header_mappings_dir = "../../../../../effect_sdk/inc/ai_tob/"
  s.platform     = :ios, '7.0'
  s.requires_arc = true
  s.xcconfig = { 'HEADER_SEARCH_PATHS' => ['"../../../../../../effect_sdk/inc/ai_tob/lens"','"../../../../../../effect_sdk/inc/ai_tob"','"../../../../../../effect_sdk/AILib/licbag_sdk"']}

 
  s.libraries = 'stdc++', 'z'
end
