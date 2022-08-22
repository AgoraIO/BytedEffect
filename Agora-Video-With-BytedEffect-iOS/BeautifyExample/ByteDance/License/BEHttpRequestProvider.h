#ifndef BEHttpRequestProvider_h
#define BEHttpRequestProvider_h

#include "BytedLicenseDefine.h"

class BEHttpRequestProvider: public HttpRequestProvider
{
    
public:
    bool getRequest(const RequestInfo* requestInfo, ResponseInfo& responseInfo) override;
    
    bool postRequest(const RequestInfo* requestInfo, ResponseInfo& responseInfo) override;
    
};
#endif //BEHttpRequestProvider_h
