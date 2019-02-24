package com.aurora.kernel;

import com.aurora.internalservice.InternalService;

import java.util.Map;

/**
 * Registry for (internal) services
 */
abstract class ServiceRegistry {
    Map<String, InternalService> serviceMap;
}
