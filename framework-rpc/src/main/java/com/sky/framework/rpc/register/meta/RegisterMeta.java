/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rpc.register.meta;

import lombok.Data;

import java.util.Objects;

/**
 * 元信息
 * 重写equals和hashcode使其在容器中具有唯一性
 *
 * @author
 */
public class RegisterMeta {

    private Address address = new Address();

    private ServiceMeta serviceMeta = new ServiceMeta();

    public String getHost() {
        return address.getHost();
    }

    public void setHost(String host) {
        address.setHost(host);
    }

    public int getPort() {
        return address.getPort();
    }

    public void setPort(int port) {
        address.setPort(port);
    }

    public String getGroup() {
        return serviceMeta.getGroup();
    }

    public void setGroup(String group) {
        serviceMeta.setGroup(group);
    }

    public String getServiceProviderName() {
        return serviceMeta.getServiceProviderName();
    }

    public void setServiceProviderName(String serviceProviderName) {
        serviceMeta.setServiceProviderName(serviceProviderName);
    }

    public String getVersion() {
        return serviceMeta.getVersion();
    }

    public void setVersion(String version) {
        serviceMeta.setVersion(version);
    }

    public Address getAddress() {
        return address;
    }

    public ServiceMeta getServiceMeta() {
        return serviceMeta;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterMeta that = (RegisterMeta) o;
        return !(Objects.nonNull(address) ? !address.equals(that.address) : that.address != null)
                && !(Objects.nonNull(serviceMeta) ? !serviceMeta.equals(that.serviceMeta) : that.serviceMeta != null);
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (serviceMeta != null ? serviceMeta.hashCode() : 0);
        return result;
    }

    @Data
    public static class Address {
        /**
         * 地址
         */
        private String host;
        /**
         * 端口
         */
        private int port;

        public Address() {
        }

        public Address(String host, int port) {
            this.host = host;
            this.port = port;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Address address = (Address) o;
            return port == address.port && !(Objects.nonNull(host) ? !host.equals(address.host) : address.host != null);
        }

        @Override
        public int hashCode() {
            int result = host != null ? host.hashCode() : 0;
            result = 31 * result + port;
            return result;
        }
    }

    /**
     *
     */
    @Data
    public static class ServiceMeta {
        /**
         * 组名
         */
        private String group;
        /**
         * 接口名称或者泛化时服务名
         */
        private String serviceProviderName;
        /**
         * 版本信息
         */
        private String version;

        public ServiceMeta() {
        }

        public ServiceMeta(String group, String serviceProviderName, String version) {
            this.group = group;
            this.serviceProviderName = serviceProviderName;
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ServiceMeta that = (ServiceMeta) o;

            return !(Objects.nonNull(group) ? !group.equals(that.group) : that.group != null)
                    && !(Objects.nonNull(serviceProviderName) ? !serviceProviderName.equals(that.serviceProviderName) : that.serviceProviderName != null)
                    && !(Objects.nonNull(version) ? !version.equals(that.version) : that.version != null);
        }

        @Override
        public int hashCode() {
            int result = group != null ? group.hashCode() : 0;
            result = 31 * result + (serviceProviderName != null ? serviceProviderName.hashCode() : 0);
            result = 31 * result + (version != null ? version.hashCode() : 0);
            return result;
        }
    }
}
