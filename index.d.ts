declare namespace AzureNotificationHub {
  export interface RegistrationConfig {
    connectionString: string;
    hubName: string;
    senderID: string;
    tags: string[];
    channelName: string;
    channelImportance: boolean;
    channelShowBadge: boolean;
    channelEnableLights: boolean;
    channelEnableVibration: boolean;
  }

  export interface TemplateRegistrationConfig extends RegistrationConfig {
    templateName: string;
    template: string;
  }

  export interface RegistrationResponse {
    uuid: string;
  }
}

declare class AzureNotificationHub {
  static register(config: AzureNotificationHub.RegistrationConfig): Promise<AzureNotificationHub.RegistrationResponse>;

  static unregister(): Promise<void>;

  static getUUID(): Promise<string>;

  static getInitialNotification<T>(): Promise<T>;
}

export = AzureNotificationHub;
